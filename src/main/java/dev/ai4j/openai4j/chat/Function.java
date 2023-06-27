package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Function {

    private final String name;
    private final String description;
    private final Parameters parameters;

    private Function(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameters = builder.parameters;
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

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Function
                && equalTo((Function) another);
    }

    private boolean equalTo(Function another) {
        return Objects.equals(name, another.name)
                && Objects.equals(description, another.description)
                && Objects.equals(parameters, another.parameters);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(description);
        h += (h << 5) + Objects.hashCode(parameters);
        return h;
    }

    @Override
    public String toString() {
        return "Function{"
                + "name=" + name
                + ", description=" + description
                + ", parameters=" + parameters
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private Parameters parameters;

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

        @Experimental
        public Builder addProperty(String name, Constraint... constraints) {
            addOptionalProperty(name, constraints);
            this.parameters.required().add(name);
            return this;
        }

        @Experimental
        public Builder addOptionalProperty(String name, Constraint... constraints) {
            if (this.parameters == null) {
                this.parameters = Parameters.builder().build();
            }

            Map<String, Object> constraintMap = new HashMap<>();
            for (Constraint constraint : constraints) {
                constraintMap.put(constraint.key(), constraint.value());
            }

            this.parameters.properties().put(name, constraintMap);
            return this;
        }

        public Function build() {
            return new Function(this);
        }
    }
}
