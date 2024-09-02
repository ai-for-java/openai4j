package dev.ai4j.openai4j.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JsonArraySchema.class, name = "array"),
        @JsonSubTypes.Type(value = JsonBooleanSchema.class, name = "boolean"),
        @JsonSubTypes.Type(value = JsonEnumSchema.class, name = "string"), // enum is serialized with string type
        @JsonSubTypes.Type(value = JsonIntegerSchema.class, name = "integer"),
        @JsonSubTypes.Type(value = JsonNumberSchema.class, name = "number"),
        @JsonSubTypes.Type(value = JsonObjectSchema.class, name = "object"),
        @JsonSubTypes.Type(value = JsonStringSchema.class, name = "string"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class JsonSchemaElement {

    @JsonProperty
    private final String type;

    protected JsonSchemaElement(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
