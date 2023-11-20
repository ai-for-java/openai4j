package dev.ai4j.openai4j.chat;

import java.util.Objects;

public final class ChatCompletionChoice {

    private final Integer index;
    private final MessageResponse message;
    private final Delta delta;
    private final String finishReason;

    private ChatCompletionChoice(Builder builder) {
        this.index = builder.index;
        this.message = builder.message;
        this.delta = builder.delta;
        this.finishReason = builder.finishReason;
    }

    public Integer index() {
        return index;
    }

    public MessageResponse message() {
        return message;
    }

    public Delta delta() {
        return delta;
    }

    public String finishReason() {
        return finishReason;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ChatCompletionChoice
                && equalTo((ChatCompletionChoice) another);
    }

    private boolean equalTo(ChatCompletionChoice another) {
        return Objects.equals(index, another.index)
                && Objects.equals(message, another.message)
                && Objects.equals(delta, another.delta)
                && Objects.equals(finishReason, another.finishReason);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(index);
        h += (h << 5) + Objects.hashCode(message);
        h += (h << 5) + Objects.hashCode(delta);
        h += (h << 5) + Objects.hashCode(finishReason);
        return h;
    }

    @Override
    public String toString() {
        return "ChatCompletionChoice{"
                + "index=" + index
                + ", message=" + message
                + ", delta=" + delta
                + ", finishReason=" + finishReason
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Integer index;
        private MessageResponse message;
        private Delta delta;
        private String finishReason;

        private Builder() {
        }

        public Builder index(Integer index) {
            this.index = index;
            return this;
        }

        public Builder message(MessageResponse message) {
            this.message = message;
            return this;
        }

        public Builder delta(Delta delta) {
            this.delta = delta;
            return this;
        }

        public Builder finishReason(String finishReason) {
            this.finishReason = finishReason;
            return this;
        }

        public ChatCompletionChoice build() {
            return new ChatCompletionChoice(this);
        }
    }
}
