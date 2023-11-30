package dev.ai4j.openai4j.embedding;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public final class Embedding {

    private final List<Float> embedding;
    private final Integer index;

    private Embedding(Builder builder) {
        this.embedding = builder.embedding;
        this.index = builder.index;
    }

    public List<Float> embedding() {
        return embedding;
    }

    public Integer index() {
        return index;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Embedding
                && equalTo((Embedding) another);
    }

    private boolean equalTo(Embedding another) {
        return Objects.equals(embedding, another.embedding)
                && Objects.equals(index, another.index);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(embedding);
        h += (h << 5) + Objects.hashCode(index);
        return h;
    }

    @Override
    public String toString() {
        return "Embedding{"
                + "embedding=" + embedding
                + ", index=" + index
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<Float> embedding;
        private Integer index;

        private Builder() {
        }

        public Builder embedding(List<Float> embedding) {
            if (embedding != null) {
                this.embedding = unmodifiableList(embedding);
            }
            return this;
        }

        public Builder index(Integer index) {
            this.index = index;
            return this;
        }

        public Embedding build() {
            return new Embedding(this);
        }
    }
}
