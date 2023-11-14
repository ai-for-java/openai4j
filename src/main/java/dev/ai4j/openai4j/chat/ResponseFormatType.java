package dev.ai4j.openai4j.chat;

public enum ResponseFormatType {

    TEXT("text "),
    JSON_OBJECT("json_object");

    private final String stringValue;

    ResponseFormatType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }

}
