package dev.ai4j.openai4j.chat;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_4_VISION_PREVIEW;
import static dev.ai4j.openai4j.chat.ChatCompletionTest.*;
import static dev.ai4j.openai4j.chat.ResponseFormatType.*;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

class RequestDisabledChatCompletionAsyncTest {

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        // when
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(USER_MESSAGE)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
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

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        assertThat(future.isCompletedExceptionally()).isTrue();
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }
}
