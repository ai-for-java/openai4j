package dev.ai4j.openai4j.embedding;

public enum EmbeddingModel {

    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),

    TEXT_EMBEDDING_3_SMALL("text-embedding-3-small"),
    TEXT_EMBEDDING_3_LARGE("text-embedding-3-large");

    private final String value;

    EmbeddingModel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
