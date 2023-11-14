package dev.ai4j.openai4j.chat;

public enum ToolType {

    FUNCTION("function");

    private final String stringValue;

    ToolType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }

}
