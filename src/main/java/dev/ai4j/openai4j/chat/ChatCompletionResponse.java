package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;
import dev.ai4j.openai4j.shared.Usage;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public final class ChatCompletionResponse {

    private final String id;
    private final Integer created;
    private final String model;
    private final List<ChatCompletionChoice> choices;
    private final Usage usage;

    private ChatCompletionResponse(Builder builder) {
        this.id = builder.id;
        this.created = builder.created;
        this.model = builder.model;
        this.choices = builder.choices;
        this.usage = builder.usage;
    }

    public String id() {
        return id;
    }

    public Integer created() {
        return created;
    }

    public String model() {
        return model;
    }

    public List<ChatCompletionChoice> choices() {
        return choices;
    }

    public Usage usage() {
        return usage;
    }

    /**
     * Convenience method to get the content of the message from the first choice.
     */
    @Experimental
    public String content() {
        return choices().get(0).message().content();
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ChatCompletionResponse
                && equalTo((ChatCompletionResponse) another);
    }

    private boolean equalTo(ChatCompletionResponse another) {
        return id.equals(another.id)
                && created.equals(another.created)
                && model.equals(another.model)
                && choices.equals(another.choices)
                && usage.equals(another.usage);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + id.hashCode();
        h += (h << 5) + created.hashCode();
        h += (h << 5) + model.hashCode();
        h += (h << 5) + choices.hashCode();
        h += (h << 5) + usage.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "ChatCompletionResponse{"
                + "id=" + id
                + ", created=" + created
                + ", model=" + model
                + ", choices=" + choices
                + ", usage=" + usage
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String id;
        private Integer created;
        private String model;
        private List<ChatCompletionChoice> choices;
        private Usage usage;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder created(Integer created) {
            this.created = created;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder choices(List<ChatCompletionChoice> choices) {
            if (choices == null) {
                return this;
            }
            this.choices = unmodifiableList(choices);
            return this;
        }

        public Builder usage(Usage usage) {
            this.usage = usage;
            return this;
        }

        public ChatCompletionResponse build() {
            return new ChatCompletionResponse(this);
        }
    }
}
