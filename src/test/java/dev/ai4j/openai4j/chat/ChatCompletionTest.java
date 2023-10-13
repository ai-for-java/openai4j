package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.FunctionCallUtil;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.Message.functionMessage;
import static dev.ai4j.openai4j.chat.Message.userMessage;
import static dev.ai4j.openai4j.chat.Role.ASSISTANT;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionTest extends RateLimitAwareTest {

    private static final String USER_MESSAGE = "Write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        String response = client.chatCompletion(USER_MESSAGE).execute();

        assertThat(response).containsIgnoringCase("hello world");
    }

    @MethodSource
    @ParameterizedTest
    void testCustomizableApi(ChatCompletionRequest request) {

        ChatCompletionResponse response = client.chatCompletion(request).execute();


        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().role()).isEqualTo(ASSISTANT);
        assertThat(response.choices().get(0).message().content()).containsIgnoringCase("hello world");

        assertThat(response.content()).containsIgnoringCase("hello world");
    }

    static Stream<Arguments> testCustomizableApi() {
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

    @Test
    void testFunctions() {

        Message userMessage = userMessage("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo-0613")
                .messages(userMessage)
                .functions(Function.builder()
                        .name("get_current_weather")
                        .description("Get the current weather in a given location")
                        .addParameter("location", STRING, description("The city and state, e.g. San Francisco, CA"))
                        .addOptionalParameter("unit", STRING, enums(Unit.class))
                        .build())
                .build();

        ChatCompletionResponse response = client.chatCompletion(request).execute();

        Message assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.role()).isEqualTo(ASSISTANT);
        assertThat(assistantMessage.content()).isNull();

        FunctionCall functionCall = assistantMessage.functionCall();
        assertThat(functionCall.name()).isEqualTo("get_current_weather");
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = FunctionCallUtil.argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSize(1);
        assertThat(arguments.get("location").toString()).contains("Boston");

        String location = FunctionCallUtil.argument(functionCall, "location");
        String unit = FunctionCallUtil.argument(functionCall, "unit");

        String weatherApiResponse = getCurrentWeather(location, unit == null ? null : Unit.valueOf(unit));

        Message functionMessage = functionMessage("get_current_weather", weatherApiResponse);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo-0613")
                .messages(userMessage, assistantMessage, functionMessage)
                .build();

        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        assertThat(secondResponse.content()).contains("22");
    }

    public static String getCurrentWeather(String location, Unit unit) {
        System.out.println(location);
        System.out.println(unit);
        return "{ \"temperature\": 22, \"unit\": \"celsius\", \"description\": \"Sunny\" }";
    }

    enum Unit {
        CELSIUS, FAHRENHEIT
    }
}
