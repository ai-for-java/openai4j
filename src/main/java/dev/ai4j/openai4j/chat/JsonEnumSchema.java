package dev.ai4j.openai4j.chat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class JsonEnumSchema extends JsonSchemaElement {

    private final String description;
    @SerializedName("enum")
    private final List<String> enumValues;

    public JsonEnumSchema(Builder builder) {
        super("string");
        this.description = builder.description;
        this.enumValues = new ArrayList<>(builder.enumValues);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof JsonEnumSchema
                && equalTo((JsonEnumSchema) another);
    }

    private boolean equalTo(JsonEnumSchema another) {
        return Objects.equals(description, another.description)
                && Objects.equals(enumValues, another.enumValues);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(description);
        h += (h << 5) + Objects.hashCode(enumValues);
        return h;
    }

    @Override
    public String toString() {
        return "JsonEnumSchema{" +
                "description=" + description +
                ", enumValues=" + enumValues +
                "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String description;
        private List<String> enumValues;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder enumValues(List<String> enumValues) {
            this.enumValues = enumValues;
            return this;
        }

        public Builder enumValues(Class<?> enumClass) {
            if (!enumClass.isEnum()) {
                throw new RuntimeException("Class " + enumClass.getName() + " must be enum");
            }

            List<String> enumValues = stream(enumClass.getEnumConstants())
                    .map(Object::toString)
                    .collect(toList());

            return enumValues(enumValues);
        }

        public JsonEnumSchema build() {
            return new JsonEnumSchema(this);
        }
    }
}
