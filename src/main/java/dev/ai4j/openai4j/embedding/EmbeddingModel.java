package dev.ai4j.openai4j.embedding;

public enum EmbeddingModel {

    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002");

    private final String value;

    EmbeddingModel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
