package dev.ai4j.openai4j.chat;

public enum ContentType {

    TEXT("text"),
    IMAGE_URL("image_url");

    private final String stringValue;

    ContentType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }


}
