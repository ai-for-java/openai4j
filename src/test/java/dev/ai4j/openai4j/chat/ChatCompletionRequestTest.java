package dev.ai4j.openai4j.chat;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionRequestTest {

    @Test
    void should_add_messages_as_list() {

        SystemMessage systemMessage = SystemMessage.from("system");
        UserMessage userMessage = UserMessage.from("user");
        AssistantMessage assistantMessage = AssistantMessage.from("user");
        ToolMessage toolMessage = ToolMessage.from("id", "tool");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(asList(systemMessage, userMessage, assistantMessage, toolMessage))
                .build();

        assertThat(request.messages()).containsExactly(systemMessage, userMessage, assistantMessage, toolMessage);
    }

    @Test
    void should_add_messages_as_varargs() {

        SystemMessage systemMessage = SystemMessage.from("system");
        UserMessage userMessage = UserMessage.from("user");
        AssistantMessage assistantMessage = AssistantMessage.from("assistant");
        ToolMessage toolMessage = ToolMessage.from("id", "tool");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(systemMessage, userMessage, assistantMessage, toolMessage)
                .build();

        assertThat(request.messages()).containsExactly(systemMessage, userMessage, assistantMessage, toolMessage);
    }

    @Test
    void should_add_messages_using_convenience_methods() {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addSystemMessage("system")
                .addUserMessage("user")
                .addAssistantMessage("assistant")
                .addToolMessage("id", "tool")
                .build();

        assertThat(request.messages()).containsExactly(
                SystemMessage.from("system"),
                UserMessage.from("user"),
                AssistantMessage.from("assistant"),
                ToolMessage.from("id", "tool")
        );
    }
}