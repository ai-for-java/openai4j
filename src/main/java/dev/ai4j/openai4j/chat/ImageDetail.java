package dev.ai4j.openai4j.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImageDetail {

    @JsonProperty("low")
    LOW,
    @JsonProperty("high")
    HIGH,
    @JsonProperty("auto")
    AUTO
}
