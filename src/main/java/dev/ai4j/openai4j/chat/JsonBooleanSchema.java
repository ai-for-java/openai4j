package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class JsonBooleanSchema extends JsonSchemaElement {

    private final String description;

    public JsonBooleanSchema(Builder builder) {
        super("boolean");
        this.description = builder.description;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof JsonBooleanSchema
                && equalTo((JsonBooleanSchema) another);
    }

    private boolean equalTo(JsonBooleanSchema another) {
        return Objects.equals(description, another.description);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(description);
        return h;
    }

    @Override
    public String toString() {
        return "JsonBooleanSchema{" +
                "description=" + description +
                "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String description;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public JsonBooleanSchema build() {
            return new JsonBooleanSchema(this);
        }
    }
}
