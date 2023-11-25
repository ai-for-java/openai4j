package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.ai4j.openai4j.chat.Role.*;

public final class MessageResponse {

    private final Role role;
    private final String content;
    private final String name;

    @Deprecated
    private final FunctionCall functionCall;
    private final List<ToolCalls> toolCalls;


    private MessageResponse(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
        this.name = builder.name;
        this.functionCall = builder.functionCall;
        this.toolCalls = builder.toolCalls;
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

    public List<ToolCalls> toolCalls() {
        return toolCalls;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof MessageResponse
                && equalTo((MessageResponse) another);
    }

    private boolean equalTo(MessageResponse another) {
        return Objects.equals(role, another.role)
                && Objects.equals(content, another.content)
                && Objects.equals(name, another.name)
                && Objects.equals(functionCall, another.functionCall)
                && Objects.equals(toolCalls, another.toolCalls);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(role);
        h += (h << 5) + Objects.hashCode(content);
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(functionCall);
        h += (h << 5) + Objects.hashCode(toolCalls);
        return h;
    }

    @Override
    public String toString() {
        return "Message{"
                + "role=" + role
                + ", content=" + content
                + ", name=" + name
                + ", functionCall=" + functionCall
                + ", toolCalls=" + toolCalls
                + "}";
    }

    @Experimental
    public static Message assistantMessage(String content) {
        Content userContent = Content.builder().type(ContentType.TEXT.stringValue()).text(content).build();
        return Message.builder()
                .role(ASSISTANT)
                .content(Arrays.asList(userContent))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Role role;
        private String content;
        private String name;
        @Deprecated
        private FunctionCall functionCall;
        private List<ToolCalls> toolCalls;

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

        @Deprecated
        public MessageResponse.Builder functionCall(FunctionCall functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public Builder toolCalls(List<ToolCalls> toolCalls) {
            this.toolCalls = toolCalls;
            return this;
        }

        public MessageResponse build() {
            return new MessageResponse(this);
        }
    }
}
