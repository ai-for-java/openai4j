package dev.ai4j.openai4j.embedding;

import java.util.List;
import java.util.Objects;

import static dev.ai4j.openai4j.embedding.EmbeddingModel.TEXT_EMBEDDING_ADA_002;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public final class EmbeddingRequest {

    private final String model;
    private final List<String> input;
    private final Integer dimensions;
    private final String user;

    private EmbeddingRequest(Builder builder) {
        this.model = builder.model;
        this.input = builder.input;
        this.dimensions = builder.dimensions;
        this.user = builder.user;
    }

    public String model() {
        return model;
    }

    public List<String> input() {
        return input;
    }

    public Integer dimensions() {
        return dimensions;
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
                && Objects.equals(dimensions, another.dimensions)
                && Objects.equals(user, another.user);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(model);
        h += (h << 5) + Objects.hashCode(input);
        h += (h << 5) + Objects.hashCode(dimensions);
        h += (h << 5) + Objects.hashCode(user);
        return h;
    }

    @Override
    public String toString() {
        return "EmbeddingRequest{"
                + "model=" + model
                + ", input=" + input
                + ", dimensions=" + dimensions
                + ", user=" + user
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String model = TEXT_EMBEDDING_ADA_002.toString();
        private List<String> input;
        private Integer dimensions;
        private String user;

        private Builder() {
        }

        public Builder model(EmbeddingModel model) {
            return model(model.toString());
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder input(String... input) {
            return input(asList(input));
        }

        public Builder input(List<String> input) {
            if (input != null) {
                this.input = unmodifiableList(input);
            }
            return this;
        }

        public Builder dimensions(Integer dimensions) {
            this.dimensions = dimensions;
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
