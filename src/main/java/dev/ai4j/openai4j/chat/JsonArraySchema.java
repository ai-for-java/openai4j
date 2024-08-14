package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class JsonArraySchema extends JsonSchemaElement {

    private final String description;
    private final JsonSchemaElement items;

    public JsonArraySchema(Builder builder) {
        super("array");
        this.description = builder.description;
        this.items = builder.items;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof JsonArraySchema
                && equalTo((JsonArraySchema) another);
    }

    private boolean equalTo(JsonArraySchema another) {
        return Objects.equals(description, another.description)
                && Objects.equals(items, another.items);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(description);
        h += (h << 5) + Objects.hashCode(items);
        return h;
    }

    @Override
    public String toString() {
        return "JsonArraySchema{" +
                "description=" + description +
                ", items=" + items +
                "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String description;
        private JsonSchemaElement items;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder items(JsonSchemaElement items) {
            this.items = items;
            return this;
        }

        public JsonArraySchema build() {
            return new JsonArraySchema(this);
        }
    }
}
