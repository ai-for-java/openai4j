package dev.ai4j.openai4j.chat;

public abstract class JsonSchemaElement {

    private final String type;

    protected JsonSchemaElement(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
