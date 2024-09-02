package dev.ai4j.openai4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ai4j.openai4j.chat.*;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import dev.ai4j.openai4j.shared.Usage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void chatCompletionResponse() throws JsonProcessingException {
        String content = "Paris";

        List<ToolCall> toolCalls = new ArrayList<>();
        ToolCall toolCall = ToolCall.builder()
                .id("call_123")
                .type(ToolType.FUNCTION)
                .function(FunctionCall.builder()
                        .name("get_capital")
                        .arguments("{\"country\": \"France\"}")
                        .build())
                .build();
        toolCalls.add(toolCall);

        List<ChatCompletionChoice> choices = new ArrayList<>();
        ChatCompletionChoice choice = ChatCompletionChoice.builder()
                .index(0)
                .delta(Delta.builder()
                        .content(content)
                        .toolCalls(toolCalls)
                        .role(Role.ASSISTANT)
                        .build())
                .message(AssistantMessage.builder()
                        .toolCalls(toolCalls)
                        .build())
                .build();
        choices.add(choice);

        ChatCompletionResponse response = ChatCompletionResponse.builder()
                .id("chatcmpl-123")
                .created(1234567890)
                .model("gpt-3.5-turbo")
                .choices(choices)
                .usage(Usage.builder()
                        .promptTokens(10)
                        .completionTokens(2)
                        .totalTokens(12)
                        .build()
                )
                .build();

        String json = objectMapper.writeValueAsString(response);
        assertThat(json).contains(content);
        assertThat(json).contains("tool_calls");

        ChatCompletionResponse duplicate = objectMapper.readValue(json, ChatCompletionResponse.class);
        assertEquals(response, duplicate);
    }

    @Test
    public void deserializeChatCompletionResponse() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/ChatCompletionResponse.json");
        ChatCompletionResponse chatCompletionResponse = objectMapper.readValue(inputStream, ChatCompletionResponse.class);
        AssistantMessage message = chatCompletionResponse.choices().get(0).message();
        assertNotNull(message.toolCalls());
    }

    @Test
    public void deserializeModerationResponse() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/ModerationResponse.json");
        ModerationResponse moderationResponse = objectMapper.readValue(inputStream, ModerationResponse.class);
        assertThat(moderationResponse.results().get(0).categories().hateThreatening()).isFalse();
    }
}
