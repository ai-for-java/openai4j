package dev.ai4j.openai4j.chat;

import java.util.Objects;

public final class Delta {

    private final Role role;
    private final String content;
    private final FunctionCall functionCall;

    private Delta(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
        this.functionCall = builder.functionCall;
    }

    public Role role() {
        return role;
    }

    public String content() {
        return content;
    }

    public FunctionCall functionCall() {
        return functionCall;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Delta
                && equalTo((Delta) another);
    }

    private boolean equalTo(Delta another) {
        return Objects.equals(role, another.role)
                && Objects.equals(content, another.content)
                && Objects.equals(functionCall, another.functionCall);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(role);
        h += (h << 5) + Objects.hashCode(content);
        h += (h << 5) + Objects.hashCode(functionCall);
        return h;
    }

    @Override
    public String toString() {
        return "Delta{"
                + "role=" + role
                + ", content=" + content
                + ", functionCall=" + functionCall
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Role role;
        private String content;
        private FunctionCall functionCall;

        private Builder() {
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder functionCall(FunctionCall functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public Delta build() {
            return new Delta(this);
        }
    }
}
