package dev.ai4j.openai4j.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ContentType {

    @JsonProperty("text")
    TEXT,
    @JsonProperty("image_url")
    IMAGE_URL
}
