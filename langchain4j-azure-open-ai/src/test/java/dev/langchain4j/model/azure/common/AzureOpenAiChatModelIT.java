package dev.langchain4j.model.azure.common;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.common.AbstractChatModelIT;

import java.util.List;

class AzureOpenAiChatModelIT extends AbstractChatModelIT {

    static final AzureOpenAiChatModel AZURE_OPEN_AI_CHAT_MODEL = AzureOpenAiChatModel.builder()
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .deploymentName("gpt-4o-mini")
            .build();

    @Override
    protected List<ChatLanguageModel> models() {
        return List.of(AZURE_OPEN_AI_CHAT_MODEL);
    }

    @Override
    protected boolean supportsJsonResponseFormat() {
        return false; // TODO implement
    }

    @Override
    protected boolean supportsJsonResponseFormatWithSchema() {
        return false; // TODO implement
    }

    protected boolean assertFinishReason() {
        return false; // TODO fix
    }
}