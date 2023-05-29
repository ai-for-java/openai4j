package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiService;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.ai4j.openai4j.chat.Message.userMessage;
import static dev.ai4j.openai4j.chat.Role.ASSISTANT;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionTest extends RateLimitAwareTest {

    private static final String USER_MESSAGE = "Write exactly the following 2 words: 'hello world'";

    private final OpenAiService openAiService = new OpenAiService(System.getenv("OPENAI_API_KEY"));

    static Stream<Arguments> testWithBuilder() {
        return Stream.of(
                Arguments.of(
                        ChatCompletionRequest.builder()
                                .messages(singletonList(userMessage(USER_MESSAGE)))
                                .build()
                ),
                Arguments.of(
                        ChatCompletionRequest.builder()
                                .messages(userMessage(USER_MESSAGE))
                                .build()
                ),
                Arguments.of(
                        ChatCompletionRequest.builder()
                                .addUserMessage(USER_MESSAGE)
                                .build()
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void testWithBuilder(ChatCompletionRequest request) {

        ChatCompletionResponse response = openAiService.getChatCompletions(request);


        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().role()).isEqualTo(ASSISTANT);
        assertThat(response.choices().get(0).message().content()).containsIgnoringCase("hello world");

        assertThat(response.content()).containsIgnoringCase("hello world");
    }

    @Test
    void testWithUserMessage() {

        String response = openAiService.getChatCompletion(USER_MESSAGE);

        assertThat(response).containsIgnoringCase("hello world");
    }
}
