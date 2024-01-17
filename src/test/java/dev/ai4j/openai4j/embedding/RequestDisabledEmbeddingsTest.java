package dev.ai4j.openai4j.embedding;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

public class RequestDisabledEmbeddingsTest {

    private static final String INPUT = "hello";

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {
        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.embedding("hello").execute())
            .withMessage("Requests to the live system are disabled");
    }

    @MethodSource
    @ParameterizedTest
    void testCustomizableApi(EmbeddingRequest request) {
        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.embedding(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    static Stream<Arguments> testCustomizableApi() {
        return Stream.of(
                Arguments.of(
                        EmbeddingRequest.builder()
                                .input(singletonList(INPUT))
                                .build()
                ),
                Arguments.of(
                        EmbeddingRequest.builder()
                                .input(INPUT)
                                .build()
                )
        );
    }
}
