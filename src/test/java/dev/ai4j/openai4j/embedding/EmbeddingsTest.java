package dev.ai4j.openai4j.embedding;

import dev.ai4j.openai4j.OpenAiService;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddingsTest extends RateLimitAwareTest {

    private static final String INPUT = "hello";

    private final OpenAiService service = new OpenAiService(System.getenv("OPENAI_API_KEY"));

    @Test
    void testSimpleApi() {

        List<Float> embedding = service.getEmbedding("hello");

        assertThat(embedding).hasSize(1536);
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

    @MethodSource
    @ParameterizedTest
    void testCustomizableApi(EmbeddingRequest request) {

        EmbeddingResponse response = service.getEmbeddings(request);


        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).embedding()).hasSize(1536);

        assertThat(response.embedding()).hasSize(1536);
    }
}
