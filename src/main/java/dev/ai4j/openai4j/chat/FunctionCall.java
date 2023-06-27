package dev.ai4j.openai4j.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class FunctionCall {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    private final String name;
    private final String arguments;

    private FunctionCall(Builder builder) {
        this.name = builder.name;
        this.arguments = builder.arguments;
    }

    public String name() {
        return name;
    }

    public String arguments() {
        return arguments;
    }

    public Map<String, Object> argumentsAsMap() {
        return GSON.fromJson(arguments, MAP_TYPE);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof FunctionCall
                && equalTo((FunctionCall) another);
    }

    private boolean equalTo(FunctionCall another) {
        return Objects.equals(name, another.name)
                && Objects.equals(arguments, another.arguments);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(arguments);
        return h;
    }

    @Override
    public String toString() {
        return "Function{"
                + "name=" + name
                + ", arguments=" + arguments
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String arguments;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder arguments(String arguments) {
            this.arguments = arguments;
            return this;
        }

        public FunctionCall build() {
            return new FunctionCall(this);
        }
    }
}
