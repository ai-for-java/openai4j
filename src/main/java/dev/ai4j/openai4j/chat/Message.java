package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

import static dev.ai4j.openai4j.chat.Role.*;

public final class Message {

    private final Role role;
    private final String content;
    private final String name;

    private Message(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
        this.name = builder.name;
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

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Message
                && equalTo((Message) another);
    }

    private boolean equalTo(Message another) {
        return role.equals(another.role)
                && content.equals(another.content)
                && name.equals(another.name);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + role.hashCode();
        h += (h << 5) + content.hashCode();
        h += (h << 5) + name.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "Message{"
                + "role=" + role
                + ", content=" + content
                + ", name=" + name
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Role role;
        private String content;
        private String name;

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

        public Message build() {
            return new Message(this);
        }
    }
}
