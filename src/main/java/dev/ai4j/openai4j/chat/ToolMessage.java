package dev.ai4j.openai4j.chat;

import java.util.Objects;

import static dev.ai4j.openai4j.chat.Role.TOOL;

public final class ToolMessage implements Message {

    private final Role role = TOOL;
    private final String toolCallId;
    private final String content;

    private ToolMessage(Builder builder) {
        this.toolCallId = builder.toolCallId;
        this.content = builder.content;
    }

    public Role role() {
        return role;
    }

    public String toolCallId() {
        return toolCallId;
    }

    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ToolMessage
                && equalTo((ToolMessage) another);
    }

    private boolean equalTo(ToolMessage another) {
        return Objects.equals(role, another.role)
                && Objects.equals(toolCallId, another.toolCallId)
                && Objects.equals(content, another.content);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(role);
        h += (h << 5) + Objects.hashCode(toolCallId);
        h += (h << 5) + Objects.hashCode(content);
        return h;
    }

    @Override
    public String toString() {
        return "ToolMessage{"
                + "role=" + role
                + ", toolCallId=" + toolCallId
                + ", content=" + content
                + "}";
    }

    public static ToolMessage from(String toolCallId, String content) {
        return ToolMessage.builder()
                .toolCallId(toolCallId)
                .content(content)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String toolCallId;
        private String content;

        private Builder() {
        }

        public Builder toolCallId(String toolCallId) {
            this.toolCallId = toolCallId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ToolMessage build() {
            return new ToolMessage(this);
        }
    }
}
