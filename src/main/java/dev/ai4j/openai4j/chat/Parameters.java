package dev.ai4j.openai4j.chat;

import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public class Parameters {

    private final String type = "object";
    private final Map<String, Map<String, Object>> properties;
    private final List<String> required;

    private Parameters(Builder builder) {
        this.properties = builder.properties;
        this.required = builder.required;
    }

    public String type() {
        return type;
    }

    public Map<String, Map<String, Object>> properties() {
        return properties;
    }

    public List<String> required() {
        return required;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Parameters
                && equalTo((Parameters) another);
    }

    private boolean equalTo(Parameters another) {
        return Objects.equals(properties, another.properties)
                && Objects.equals(required, another.required);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(type);
        h += (h << 5) + Objects.hashCode(properties);
        h += (h << 5) + Objects.hashCode(required);
        return h;
    }

    @Override
    public String toString() {
        return "Parameters{"
                + "type=" + type
                + ", properties=" + properties
                + ", required=" + required
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Map<String, Map<String, Object>> properties = new HashMap<>();
        private List<String> required = new ArrayList<>();

        private Builder() {
        }

        public Builder properties(Map<String, Map<String, Object>> properties) {
            if (properties != null) {
                this.properties = unmodifiableMap(properties);
            }
            return this;
        }

        public Builder required(List<String> required) {
            if (required != null) {
                this.required = unmodifiableList(required);
            }
            return this;
        }

        public Parameters build() {
            return new Parameters(this);
        }
    }
}
