package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class ResponseFormat {

    private final Object type;
    private final JsonSchema jsonSchema;

    public ResponseFormat(Object type, JsonSchema jsonSchema) {
        this.type = type;
        this.jsonSchema = jsonSchema;
    }

    public Object type() {
        return type;
    }

    public JsonSchema jsonSchema() {
        return jsonSchema;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ResponseFormat
                && equalTo((ResponseFormat) another);
    }

    private boolean equalTo(ResponseFormat another) {
        return Objects.equals(type, another.type)
                && Objects.equals(jsonSchema, another.jsonSchema);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(type);
        h += (h << 5) + Objects.hashCode(jsonSchema);
        return h;
    }

    @Override
    public String toString() {
        return "ResponseFormat{" +
                "type=" + type +
                ", jsonSchema=" + jsonSchema +
                "}";
    }
}
