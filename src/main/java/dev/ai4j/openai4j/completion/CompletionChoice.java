package dev.ai4j.openai4j.completion;

import java.util.Objects;

public final class CompletionChoice {

    private final String text;
    private final Integer index;
    private final Logprobs logprobs;
    private final String finishReason;

    private CompletionChoice(Builder builder) {
        this.text = builder.text;
        this.index = builder.index;
        this.logprobs = builder.logprobs;
        this.finishReason = builder.finishReason;
    }

    public String text() {
        return text;
    }

    public Integer index() {
        return index;
    }

    public Logprobs logprobs() {
        return logprobs;
    }

    public String finishReason() {
        return finishReason;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof CompletionChoice
                && equalTo((CompletionChoice) another);
    }

    private boolean equalTo(CompletionChoice another) {
        return Objects.equals(text, another.text)
                && Objects.equals(index, another.index)
                && Objects.equals(logprobs, another.logprobs)
                && Objects.equals(finishReason, another.finishReason);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(text);
        h += (h << 5) + Objects.hashCode(index);
        h += (h << 5) + Objects.hashCode(logprobs);
        h += (h << 5) + Objects.hashCode(finishReason);
        return h;
    }

    @Override
    public String toString() {
        return "CompletionChoice{"
                + "text=" + text
                + ", index=" + index
                + ", logprobs=" + logprobs
                + ", finishReason=" + finishReason
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String text;
        private Integer index;
        private Logprobs logprobs;
        private String finishReason;

        private Builder() {
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder index(Integer index) {
            this.index = index;
            return this;
        }

        public Builder logprobs(Logprobs logprobs) {
            this.logprobs = logprobs;
            return this;
        }

        public Builder finishReason(String finishReason) {
            this.finishReason = finishReason;
            return this;
        }

        public CompletionChoice build() {
            return new CompletionChoice(this);
        }
    }
}
