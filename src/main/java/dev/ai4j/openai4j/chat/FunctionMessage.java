package dev.ai4j.openai4j.chat;

import java.util.Objects;

import static dev.ai4j.openai4j.chat.Role.FUNCTION;

@Deprecated
public final class FunctionMessage implements Message {

    private final Role role = FUNCTION;
    private final String name;
    private final String content;

    private FunctionMessage(Builder builder) {
        this.name = builder.name;
        this.content = builder.content;
    }

    public Role role() {
        return role;
    }

    public String name() {
        return name;
    }

    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof FunctionMessage
                && equalTo((FunctionMessage) another);
    }

    private boolean equalTo(FunctionMessage another) {
        return Objects.equals(role, another.role)
                && Objects.equals(name, another.name)
                && Objects.equals(content, another.content);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(role);
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(content);
        return h;
    }

    @Override
    public String toString() {
        return "FunctionMessage{"
                + "role=" + role
                + ", name=" + name
                + ", content=" + content
                + "}";
    }

    @Deprecated
    public static FunctionMessage from(String name, String content) {
        return FunctionMessage.builder()
                .name(name)
                .content(content)
                .build();
    }

    @Deprecated
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String content;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public FunctionMessage build() {
            return new FunctionMessage(this);
        }
    }
}
