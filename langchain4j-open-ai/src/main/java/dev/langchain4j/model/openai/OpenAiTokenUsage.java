package dev.langchain4j.model.openai;

import dev.langchain4j.model.output.TokenUsage;

public class OpenAiTokenUsage extends TokenUsage {

    public record InputTokensDetails( // TODO or PromptTokensDetails ?
                                      Integer cachedTokens,
                                      Integer audioTokens
    ) {
    }

    public record OutputTokensDetails( // TODO or CompletionTokensDetails ?
                                       Integer reasoningTokens,
                                       Integer audioTokens,
                                       Integer acceptedPredictionTokens,
                                       Integer rejectedPredictionTokens
    ) {
    }

    private final InputTokensDetails inputTokensDetails;
    private final OutputTokensDetails outputTokensDetails;

    private OpenAiTokenUsage(Builder builder) {
        super(builder.inputTokenCount, builder.outputTokenCount, builder.totalTokenCount);
        this.inputTokensDetails = builder.inputTokensDetails;
        this.outputTokensDetails = builder.outputTokensDetails;
    }

    public InputTokensDetails inputTokensDetails() {
        return inputTokensDetails;
    }

    public OutputTokensDetails outputTokensDetails() {
        return outputTokensDetails;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer inputTokenCount;
        private Integer outputTokenCount;
        private Integer totalTokenCount;
        private InputTokensDetails inputTokensDetails;
        private OutputTokensDetails outputTokensDetails;

        public Builder inputTokenCount(Integer inputTokenCount) {
            this.inputTokenCount = inputTokenCount;
            return this;
        }

        public Builder outputTokenCount(Integer outputTokenCount) {
            this.outputTokenCount = outputTokenCount;
            return this;
        }

        public Builder totalTokenCount(Integer totalTokenCount) {
            this.totalTokenCount = totalTokenCount;
            return this;
        }

        public Builder inputTokensDetails(InputTokensDetails inputTokensDetails) {
            this.inputTokensDetails = inputTokensDetails;
            return this;
        }

        public Builder outputTokensDetails(OutputTokensDetails outputTokensDetails) {
            this.outputTokensDetails = outputTokensDetails;
            return this;
        }

        public OpenAiTokenUsage build() {
            return new OpenAiTokenUsage(this);
        }
    }
}