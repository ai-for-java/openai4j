package dev.ai4j.openai4j;

public enum Model {

    // completions
    TEXT_DAVINCI_003("text-davinci-003"),

    // chat completions
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_4("gpt-4"),

    // embeddings
    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),

    // moderations
    TEXT_MODERATION_STABLE("text-moderation-stable"),
    TEXT_MODERATION_LATEST("text-moderation-latest");

    private final String stringValue;

    Model(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }
}
