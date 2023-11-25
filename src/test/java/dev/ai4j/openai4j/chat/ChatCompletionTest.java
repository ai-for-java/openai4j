package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.FunctionCallUtil;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.ToolCallsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.Model.GPT_4_VISION_PREVIEW;
import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.Message.functionMessage;
import static dev.ai4j.openai4j.chat.Message.userMessage;
import static dev.ai4j.openai4j.chat.MessageResponse.assistantMessage;
import static dev.ai4j.openai4j.chat.Role.ASSISTANT;
import static java.net.Proxy.Type.HTTP;
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
                .tools(Tool.builder()
                        .type(ToolType.FUNCTION.stringValue())
                        .function(Function.builder()
                                .name("get_current_weather")
                                .description("Get the current weather in a given location")
                                .addParameter("location", STRING, description("The city and state, e.g. San Francisco, CA"))
                                .addOptionalParameter("unit", STRING, enums(ChatCompletionTest.Unit.class))
                                .build())
                        .build())
                .build();

        ChatCompletionResponse response = client.chatCompletion(request).execute();

        MessageResponse assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.role()).isEqualTo(ASSISTANT);
        assertThat(assistantMessage.content()).isNull();

        List<ToolCalls> toolCalls = assistantMessage.toolCalls();
        assertThat(toolCalls.get(0).function().name()).isEqualTo("get_current_weather");
        assertThat(toolCalls.get(0).function().arguments()).isNotBlank();

        Map<String, Object> arguments = ToolCallsUtil.argumentsAsMap(toolCalls.get(0).function().arguments());
        assertThat(arguments).hasSize(1);
        assertThat(arguments.get("location").toString()).contains("Boston");

        String location = ToolCallsUtil.argument(toolCalls.get(0).function(), "location");
        String unit = ToolCallsUtil.argument(toolCalls.get(0).function(), "unit");


        String weatherApiResponse = getCurrentWeather(location, unit == null ? null : Unit.valueOf(unit));

        Message functionMessage = functionMessage("get_current_weather", weatherApiResponse);

        Message assistantAssembleMessage = assistantMessage(assistantMessage.content());

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo-0613")
                .messages(userMessage, assistantAssembleMessage, functionMessage)
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

    @Test
    void testImageMessageApi() {
        String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";
        String text = "What’s in this image?";
        ImageUrl imageUrl = ImageUrl.builder().url(url).build();
        Content imageContent = Content.builder().type(ContentType.IMAGE_URL.stringValue()).imageUrl(imageUrl).build();
        Content textContent = Content.builder().type(ContentType.TEXT.stringValue()).text(text).build();
        List<Content> list = Arrays.asList(textContent,imageContent);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4_VISION_PREVIEW)
                .messages(userMessage(list))
                .maxTokens(500)
                .build();
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        MessageResponse assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).containsIgnoringCase("green");
    }

}
