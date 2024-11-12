package dev.langchain4j.service.output;

import dev.langchain4j.Experimental;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.TypeUtils;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static dev.langchain4j.exception.IllegalConfigurationException.illegalConfiguration;
import static dev.langchain4j.model.chat.request.json.JsonSchemaElementHelper.jsonObjectOrReferenceSchemaFrom;
import static dev.langchain4j.service.TypeUtils.getRawClass;
import static dev.langchain4j.service.TypeUtils.resolveFirstGenericParameterClass;
import static dev.langchain4j.service.TypeUtils.typeHasRawClass;
import static java.util.Arrays.stream;

@Experimental
public class JsonSchemas {

    public static Optional<JsonSchema> jsonSchemaFrom(Type returnType) {

        if (typeHasRawClass(returnType, Result.class)) {
            returnType = resolveFirstGenericParameterClass(returnType);
        }

        // TODO validate this earlier
        if (returnType == void.class) {
            throw illegalConfiguration("Return type of method '%s' cannot be void");
        }

        if (!isPojo(returnType) && !isEnum(returnType)) {
            return Optional.empty();
        }

        if (typeHasRawClass(returnType, List.class) || typeHasRawClass(returnType, Set.class)) {
            Class<?> actualType = resolveFirstGenericParameterClass(returnType);
            if (actualType != null && actualType.isEnum()) {
                return Optional.of(arraySchemaFrom(returnType, actualType, enumSchemaFrom(actualType)));
            } else {
                return Optional.of(arraySchemaFrom(returnType, actualType, objectSchemaFrom(actualType)));
            }
        } else {
            Class<?> returnTypeClass = (Class<?>) returnType;
            if (returnTypeClass.isEnum()) {
                JsonSchema jsonSchema = JsonSchema.builder()
                    .name(returnTypeClass.getSimpleName())
                    .rootElement(JsonObjectSchema.builder()
                        .addProperty(returnTypeClass.getSimpleName(), enumSchemaFrom(returnTypeClass))
                        .build())
                    .build();
                return Optional.of(jsonSchema);
            } else {
                JsonSchema jsonSchema = JsonSchema.builder()
                    .name(returnTypeClass.getSimpleName())
                    .rootElement(objectSchemaFrom(returnTypeClass))
                    .build();
                return Optional.of(jsonSchema);
            }
        }
    }

    private static JsonSchemaElement objectSchemaFrom(Class<?> actualType) {
        return jsonObjectOrReferenceSchemaFrom(actualType, null, new LinkedHashMap<>(), true);
    }

    private static JsonEnumSchema enumSchemaFrom(Class<?> actualType) {
        return JsonEnumSchema.builder()
            .enumValues(stream(actualType.getEnumConstants()).map(Object::toString).toList())
            .build();
    }

    private static JsonSchema arraySchemaFrom(Type returnType, Class<?> actualType, JsonSchemaElement items) {
        return JsonSchema.builder()
            .name(getRawClass(returnType).getSimpleName() + "_of_" + actualType.getSimpleName())
            .rootElement(JsonObjectSchema.builder()
                .addProperty("items", JsonArraySchema.builder()
                    .items(items)
                    .build())
                .required("items")
                .build())
            .build();
    }

    static boolean isEnum(Type returnType) {
        if (returnType instanceof Class<?> && ((Class<?>) returnType).isEnum()) {
            return true;
        }

        Class<?> typeArgumentClass = TypeUtils.resolveFirstGenericParameterClass(returnType);
        return typeArgumentClass != null && typeArgumentClass.isEnum();
    }

    private static boolean isPojo(Type returnType) {

        if (returnType == String.class
            || returnType == AiMessage.class
            || returnType == TokenStream.class
            || returnType == Response.class) {
            return false;
        }

        // Explanation (which will make this a lot easier to understand):
        // In the case of List<String> these two would be set like:
        // rawClass: List.class
        // typeArgumentClass: String.class
        Class<?> rawClass = getRawClass(returnType);
        Class<?> typeArgumentClass = TypeUtils.resolveFirstGenericParameterClass(returnType);

        Optional<OutputParser<?>> outputParser = new DefaultOutputParserFactory().get(rawClass, typeArgumentClass);
        if (outputParser.isPresent()) {
            return false;
        }

        return true;
    }
}
