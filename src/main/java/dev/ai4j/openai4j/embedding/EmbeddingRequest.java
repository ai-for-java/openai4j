package dev.ai4j.openai4j.embedding;

import dev.ai4j.openai4j.Experimental;
import dev.ai4j.openai4j.Model;

import java.util.List;
import java.util.Objects;

import static dev.ai4j.openai4j.Model.TEXT_EMBEDDING_ADA_002;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public final class EmbeddingRequest {

    private final String model;
    private final List<String> input;
    private final String user;

    private EmbeddingRequest(Builder builder) {
        this.model = builder.model;
        this.input = builder.input;
        this.user = builder.user;
    }

    public String model() {
        return model;
    }

    public List<String> input() {
        return input;
    }

    public String user() {
        return user;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof EmbeddingRequest
                && equalTo((EmbeddingRequest) another);
    }

    private boolean equalTo(EmbeddingRequest another) {
        return Objects.equals(model, another.model)
                && Objects.equals(input, another.input)
                && Objects.equals(user, another.user);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(model);
        h += (h << 5) + Objects.hashCode(input);
        h += (h << 5) + Objects.hashCode(user);
        return h;
    }

    @Override
    public String toString() {
        return "EmbeddingRequest{"
                + "model=" + model
                + ", input=" + input
                + ", user=" + user
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String model = TEXT_EMBEDDING_ADA_002.stringValue();
        private List<String> input;
        private String user;

        private Builder() {
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        @Experimental
        public Builder model(Model model) {
            return model(model.stringValue());
        }

        @Experimental
        public Builder input(String... input) {
            return input(asList(input));
        }

        public Builder input(List<String> input) {
            if (input == null) {
                return this;
            }
            this.input = unmodifiableList(input);
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public EmbeddingRequest build() {
            return new EmbeddingRequest(this);
        }
    }
}
