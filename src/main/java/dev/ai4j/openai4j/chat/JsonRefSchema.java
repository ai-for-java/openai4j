package dev.ai4j.openai4j.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;

@JsonDeserialize(builder = JsonRefSchema.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JsonRefSchema extends JsonSchemaElement { // TODO name

    @JsonProperty("$ref")
    private final String ref;

    public JsonRefSchema(Builder builder) {
        super(null);
        this.ref = builder.ref;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof JsonRefSchema
                && equalTo((JsonRefSchema) another);
    }

    private boolean equalTo(JsonRefSchema another) {
        return Objects.equals(ref, another.ref);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(ref);
        return h;
    }

    @Override
    public String toString() {
        return "JsonRefSchema{" +
                "ref=" + ref +
                "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Builder {

        private String ref;

        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        public JsonRefSchema build() {
            return new JsonRefSchema(this);
        }
    }
}
