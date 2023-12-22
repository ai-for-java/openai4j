package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class ResponseFormat {

    private final Object type;

    public ResponseFormat(ResponseFormatType type) {
        this.type = type;
    }

    public ResponseFormat(String type) {
        this.type = type;
    }

    public Object type() {
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
                "}";
    }
}
