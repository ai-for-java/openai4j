package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class ToolCalls {


    private final String name;
    private final String arguments;

    private ToolCalls(ToolCalls.Builder builder) {
        this.name = builder.name;
        this.arguments = builder.arguments;
    }

    public String name() {
        return name;
    }

    public String arguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ToolCalls
                && equalTo((ToolCalls) another);
    }

    private boolean equalTo(ToolCalls another) {
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

    public static ToolCalls.Builder builder() {
        return new ToolCalls.Builder();
    }

    public static final class Builder {

        private String name;
        private String arguments;

        private Builder() {
        }

        public ToolCalls.Builder name(String name) {
            this.name = name;
            return this;
        }

        public ToolCalls.Builder arguments(String arguments) {
            this.arguments = arguments;
            return this;
        }

        public ToolCalls build() {
            return new ToolCalls(this);
        }
    }

}
