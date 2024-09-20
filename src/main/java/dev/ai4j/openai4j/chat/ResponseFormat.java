package dev.ai4j.openai4j.chat;

import java.util.Objects;
import java.util.Optional;

public class ResponseFormat {

    private final ResponseFormatType type;

    private final Optional<String> json_schema;

    public ResponseFormat(ResponseFormatType type) {
        this.type = type;
        this.json_schema = Optional.empty();
    }

    public ResponseFormat(String type) {
        this(ResponseFormatType.valueOf(type.toUpperCase()));
    }

    public ResponseFormat(ResponseFormatType type, String json_schema) {
        this.type = type;
        this.json_schema = Optional.of(json_schema);
    }

    public ResponseFormat(String type, String json_schema) {
        this(ResponseFormatType.valueOf(type.toUpperCase()), json_schema);
    }

    public ResponseFormatType type() {
        return type;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ResponseFormat
                && equalTo((ResponseFormat) another);
    }

    private boolean equalTo(ResponseFormat another) {
        return Objects.equals(type, another.type);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(type);
        return h;
    }

    @Override
    public String toString() {
        return "ResponseFormat{" +
                "type=" + type +
                "json_schema="+ json_schema +
                "}";
    }
}
