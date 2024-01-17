package dev.ai4j.openai4j.chat;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_4_VISION_PREVIEW;
import static dev.ai4j.openai4j.chat.ChatCompletionTest.*;
import static dev.ai4j.openai4j.chat.ResponseFormatType.*;
import static dev.ai4j.openai4j.chat.ToolType.FUNCTION;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.params.provider.EnumSource.Mode.*;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

class RequestDisabledChatCompletionStreamingTest {

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .logStreamingResponses()
            .build();

    @Test
    void testSimpleApi() {

        // when
        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(USER_MESSAGE)
                .onPartialResponse(responseBuilder::append)
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

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
                .stream(false) // intentionally setting to false in order to test that it is ignored
                .stop("one", "two")
                .maxTokens(3)
                .presencePenalty(0.0)
                .frequencyPenalty(0.0)
                .logitBias(singletonMap("50256", -100))
                .user("Klaus")
                .responseFormat(TEXT)
                .seed(42)
                .build();

        // when
        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    String content = partialResponse.choices().get(0).delta().content();
                    if (content != null) {
                        responseBuilder.append(content);
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

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
    void testTools(ChatCompletionModel model)  {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .build();

        // when
        StringBuilder toolIdBuilder = new StringBuilder();
        StringBuilder functionNameBuilder = new StringBuilder();
        StringBuilder functionArgumentsBuilder = new StringBuilder();
        CompletableFuture<AssistantMessage> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();
                    assertThat(delta.content()).isNull();
                    assertThat(delta.functionCall()).isNull();

                    if (delta.toolCalls() != null) {
                        assertThat(delta.toolCalls()).hasSize(1);

                        ToolCall toolCall = delta.toolCalls().get(0);
                        assertThat(toolCall.type()).isIn(null, FUNCTION);
                        assertThat(toolCall.function()).isNotNull();

                        if (toolCall.id() != null) {
                            toolIdBuilder.append(toolCall.id());
                        }

                        FunctionCall functionCall = toolCall.function();
                        if (functionCall.name() != null) {
                            functionNameBuilder.append(functionCall.name());
                        }
                        if (functionCall.arguments() != null) {
                            functionArgumentsBuilder.append(functionCall.arguments());
                        }
                    }
                })
                .onComplete(() -> {
                    AssistantMessage assistantMessage = AssistantMessage.builder()
                            .toolCalls(ToolCall.builder()
                                    .id(toolIdBuilder.toString())
                                    .type(FUNCTION)
                                    .function(FunctionCall.builder()
                                            .name(functionNameBuilder.toString())
                                            .arguments(functionArgumentsBuilder.toString())
                                            .build())
                                    .build())
                            .build();
                    future.complete(assistantMessage);
                })
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

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
    void testFunctions(ChatCompletionModel model)  {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .build();

        // when
        StringBuilder functionNameBuilder = new StringBuilder();
        StringBuilder functionArgumentsBuilder = new StringBuilder();
        CompletableFuture<AssistantMessage> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();
                    assertThat(delta.content()).isNull();
                    assertThat(delta.toolCalls()).isNull();

                    if (delta.functionCall() != null) {
                        FunctionCall functionCall = delta.functionCall();
                        if (functionCall.name() != null) {
                            functionNameBuilder.append(functionCall.name());
                        }
                        if (functionCall.arguments() != null) {
                            functionArgumentsBuilder.append(functionCall.arguments());
                        }
                    }
                })
                .onComplete(() -> {
                    AssistantMessage assistantMessage = AssistantMessage.builder()
                            .functionCall(FunctionCall.builder()
                                    .name(functionNameBuilder.toString())
                                    .arguments(functionArgumentsBuilder.toString())
                                    .build())
                            .build();
                    future.complete(assistantMessage);
                })
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

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
    void testToolChoice(ChatCompletionModel model)  {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .toolChoice(WEATHER_TOOL_NAME)
                .build();

        // when
        StringBuilder toolIdBuilder = new StringBuilder();
        StringBuilder functionNameBuilder = new StringBuilder();
        StringBuilder functionArgumentsBuilder = new StringBuilder();
        CompletableFuture<AssistantMessage> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();
                    assertThat(delta.content()).isNull();
                    assertThat(delta.functionCall()).isNull();

                    if (delta.toolCalls() != null) {
                        assertThat(delta.toolCalls()).hasSize(1);

                        ToolCall toolCall = delta.toolCalls().get(0);
                        assertThat(toolCall.type()).isIn(null, FUNCTION);
                        assertThat(toolCall.function()).isNotNull();

                        if (toolCall.id() != null) {
                            toolIdBuilder.append(toolCall.id());
                        }

                        FunctionCall functionCall = toolCall.function();
                        if (functionCall.name() != null) {
                            functionNameBuilder.append(functionCall.name());
                        }
                        if (functionCall.arguments() != null) {
                            functionArgumentsBuilder.append(functionCall.arguments());
                        }
                    }
                })
                .onComplete(() -> {
                    AssistantMessage assistantMessage = AssistantMessage.builder()
                            .toolCalls(ToolCall.builder()
                                    .id(toolIdBuilder.toString())
                                    .type(FUNCTION)
                                    .function(FunctionCall.builder()
                                            .name(functionNameBuilder.toString())
                                            .arguments(functionArgumentsBuilder.toString())
                                            .build())
                                    .build())
                            .build();
                    future.complete(assistantMessage);
                })
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

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
    void testFunctionChoice(ChatCompletionModel model)  {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .functionCall(WEATHER_TOOL_NAME)
                .build();

        // when
        StringBuilder functionNameBuilder = new StringBuilder();
        StringBuilder functionArgumentsBuilder = new StringBuilder();
        CompletableFuture<AssistantMessage> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();
                    assertThat(delta.content()).isNull();
                    assertThat(delta.toolCalls()).isNull();

                    if (delta.functionCall() != null) {
                        FunctionCall functionCall = delta.functionCall();
                        if (functionCall.name() != null) {
                            functionNameBuilder.append(functionCall.name());
                        }
                        if (functionCall.arguments() != null) {
                            functionArgumentsBuilder.append(functionCall.arguments());
                        }
                    }
                })
                .onComplete(() -> {
                    AssistantMessage assistantMessage = AssistantMessage.builder()
                            .functionCall(FunctionCall.builder()
                                    .name(functionNameBuilder.toString())
                                    .arguments(functionArgumentsBuilder.toString())
                                    .build())
                            .build();
                    future.complete(assistantMessage);
                })
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = INCLUDE, names = {"GPT_3_5_TURBO_1106", "GPT_4_1106_PREVIEW"})
    void testJsonResponseFormat(ChatCompletionModel model)  {

        // given
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addUserMessage("Extract information from the following text:\n" +
                        "Who is Klaus Heisler?\n" +
                        "Respond in JSON format with two fields: 'name' and 'surname'")
                .responseFormat(JSON_OBJECT)
                .build();

        // when
        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();
                    if (delta.content() != null) {
                        responseBuilder.append(delta.content());
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void testGpt4Vision()  {

        // given
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4_VISION_PREVIEW)
                .messages(UserMessage.from("What is in this image?", imageUrl))
                .maxTokens(100)
                .build();

        // when
        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();
                    if (delta.content() != null) {
                        responseBuilder.append(delta.content());
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }
}
