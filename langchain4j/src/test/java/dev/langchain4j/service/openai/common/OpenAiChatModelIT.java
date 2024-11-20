package dev.langchain4j.service.openai.common;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.common.AbstractChatModelIT;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

// TODO move to langchain4j-open-ai module once dependency cycle is resolved
class OpenAiChatModelIT extends AbstractChatModelIT {

    static final OpenAiChatModel.OpenAiChatModelBuilder OPEN_AI_CHAT_MODEL_BUILDER = OpenAiChatModel.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
            .modelName(GPT_4_O_MINI);

    @Override
    protected List<ChatLanguageModel> models() {
        return List.of(
                OPEN_AI_CHAT_MODEL_BUILDER
                        .build(),
                OPEN_AI_CHAT_MODEL_BUILDER
                        .strictTools(true)
                        .build(),
                OPEN_AI_CHAT_MODEL_BUILDER
                        .responseFormat("json_schema")
                        .strictJsonSchema(true)
                        .build()
                // TODO json_object?
        );
    }
}