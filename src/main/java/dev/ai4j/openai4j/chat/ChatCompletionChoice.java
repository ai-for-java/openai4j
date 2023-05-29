package dev.ai4j.openai4j.chat;

public final class ChatCompletionChoice {

    private final Integer index;
    private final Message message;
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

    public Message message() {
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
                && equalTo(0, (ChatCompletionChoice) another);
    }

    private boolean equalTo(int synthetic, ChatCompletionChoice another) {
        return index.equals(another.index)
                && message.equals(another.message)
                && delta.equals(another.delta)
                && finishReason.equals(another.finishReason);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + index.hashCode();
        h += (h << 5) + message.hashCode();
        h += (h << 5) + delta.hashCode();
        h += (h << 5) + finishReason.hashCode();
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
        private Message message;
        private Delta delta;
        private String finishReason;

        private Builder() {
        }

        public Builder index(Integer index) {
            this.index = index;
            return this;
        }

        public Builder message(Message message) {
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
