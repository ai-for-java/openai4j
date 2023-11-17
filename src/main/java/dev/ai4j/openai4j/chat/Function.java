package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Function {

    private final String name;
    private final String description;
    private final Parameters parameters;

    private final String arguments;

    private Function(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameters = builder.parameters;
        this.arguments = builder.arguments;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Parameters parameters() {
        return parameters;
    }

    public String arguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Function
                && equalTo((Function) another);
    }

    private boolean equalTo(Function another) {
        return Objects.equals(name, another.name)
                && Objects.equals(description, another.description)
                && Objects.equals(parameters, another.parameters)
                && Objects.equals(arguments, another.arguments);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(description);
        h += (h << 5) + Objects.hashCode(parameters);
        h += (h << 5) + Objects.hashCode(arguments);
        return h;
    }

    @Override
    public String toString() {
        return "Function{"
                + "name=" + name
                + ", description=" + description
                + ", parameters=" + parameters
                + ", arguments=" + arguments
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private Parameters parameters;

        private String arguments;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameters(Parameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder arguments(String arguments) {
            this.arguments = arguments;
            return this;
        }

        @Experimental
        public Builder addParameter(String name, JsonSchemaProperty... jsonSchemaProperties) {
            addOptionalParameter(name, jsonSchemaProperties);
            this.parameters.required().add(name);
            return this;
        }

        @Experimental
        public Builder addOptionalParameter(String name, JsonSchemaProperty... jsonSchemaProperties) {
            if (this.parameters == null) {
                this.parameters = Parameters.builder().build();
            }

            Map<String, Object> jsonSchemaPropertiesMap = new HashMap<>();
            for (JsonSchemaProperty jsonSchemaProperty : jsonSchemaProperties) {
                jsonSchemaPropertiesMap.put(jsonSchemaProperty.key(), jsonSchemaProperty.value());
            }

            this.parameters.properties().put(name, jsonSchemaPropertiesMap);
            return this;
        }

        public Function build() {
            return new Function(this);
        }
    }
}
