package dev.ai4j.openai4j.chat;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_4_VISION_PREVIEW;
import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.ResponseFormatType.*;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.EnumSource.Mode.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

class RequestDisabledChatCompletionTest {

    static final String SYSTEM_MESSAGE = "Be concise";
    static final String USER_MESSAGE = "Write exactly the following 2 words: 'hello world'";

    static final String WEATHER_TOOL_NAME = "get_current_weather";
    static final Function WEATHER_FUNCTION = Function.builder()
            .name(WEATHER_TOOL_NAME)
            .description("Get the current weather in a given location")
            .addParameter("location", STRING, description("The city and state, e.g. San Francisco, CA"))
            .addOptionalParameter("unit", STRING, enums(Unit.class))
            .build();
    static final Tool WEATHER_TOOL = Tool.from(WEATHER_FUNCTION);

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {
        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(USER_MESSAGE).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
    })
    void testCustomizableApi(ChatCompletionModel model) {

        // given
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addSystemMessage(SYSTEM_MESSAGE)
                .addUserMessage(USER_MESSAGE)
                .temperature(1.0)
                .topP(0.1)
                .n(1)
                .stream(false)
                .stop("one", "two")
                .maxTokens(3)
                .presencePenalty(0.0)
                .frequencyPenalty(0.0)
                .logitBias(singletonMap("50256", -100))
                .user("Klaus")
                .responseFormat(TEXT)
                .seed(42)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including tools
    })
    void testTools(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including functions
    })
    void testFunctions(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // does not support many things now, including tools
    })
    void testToolChoice(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .toolChoice(WEATHER_TOOL_NAME)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // does not support many things now, including tools
    })
    void testFunctionChoice(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .functionCall(WEATHER_TOOL_NAME)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    enum Unit {
        CELSIUS, FAHRENHEIT
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = INCLUDE, names = {"GPT_3_5_TURBO_1106", "GPT_4_1106_PREVIEW"})
    void testJsonResponseFormat(ChatCompletionModel model) {

        // given
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addUserMessage("Extract information from the following text:\n" +
                        "Who is Klaus Heisler?\n" +
                        "Respond in JSON format with two fields: 'name' and 'surname'")
                .responseFormat(JSON_OBJECT)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void testGpt4Vision() {

        // given
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4_VISION_PREVIEW)
                .messages(UserMessage.from("What is in this image?", imageUrl))
                .maxTokens(100)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613" // I don't have access to these models
    })
    void testUserMessageWithStringContent(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.builder()
                .content("What is the capital of Germany?")
                .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.chatCompletion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }
}
