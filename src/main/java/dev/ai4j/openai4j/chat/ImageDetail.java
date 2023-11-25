package dev.ai4j.openai4j.chat;

public enum ImageDetail {

    AUTO("auto"),
    HIGH("high"),
    LOW("low");

    private final String stringValue;

    ImageDetail(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }

}
