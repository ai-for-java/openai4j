package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

import java.util.Objects;

import static dev.ai4j.openai4j.chat.Role.*;

public final class Message {

    private final Role role;
    private final String content;
    private final String name;
    private final FunctionCall functionCall;

    private Message(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
        this.name = builder.name;
        this.functionCall = builder.functionCall;
    }

    public Role role() {
        return role;
    }

    public String content() {
        return content;
    }

    public String name() {
        return name;
    }

    public FunctionCall functionCall() {
        return functionCall;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Message
                && equalTo((Message) another);
    }

    private boolean equalTo(Message another) {
        return Objects.equals(role, another.role)
                && Objects.equals(content, another.content)
                && Objects.equals(name, another.name)
                && Objects.equals(functionCall, another.functionCall);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(role);
        h += (h << 5) + Objects.hashCode(content);
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(functionCall);
        return h;
    }

    @Override
    public String toString() {
        return "Message{"
                + "role=" + role
                + ", content=" + content
                + ", name=" + name
                + ", functionCall=" + functionCall
                + "}";
    }

    @Experimental
    public static Message systemMessage(String content) {
        return Message.builder()
                .role(SYSTEM)
                .content(content)
                .build();
    }

    @Experimental
    public static Message userMessage(String content) {
        return Message.builder()
                .role(USER)
                .content(content)
                .build();
    }

    @Experimental
    public static Message assistantMessage(String content) {
        return Message.builder()
                .role(ASSISTANT)
                .content(content)
                .build();
    }

    @Experimental
    public static Message functionMessage(String name, String content) {
        return Message.builder()
                .role(FUNCTION)
                .name(name)
                .content(content)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Role role;
        private String content;
        private String name;
        private FunctionCall functionCall;

        private Builder() {
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        @Experimental
        public Builder role(String role) {
            return role(Role.from(role));
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder functionCall(FunctionCall functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
