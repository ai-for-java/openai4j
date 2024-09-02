package dev.ai4j.openai4j.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ToolChoiceMode {

    @JsonProperty("none")
    NONE,
    @JsonProperty("auto")
    AUTO,
    @JsonProperty("required")
    REQUIRED
}
