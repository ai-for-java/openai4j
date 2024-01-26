package dev.ai4j.openai4j.embedding;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

public class EmbeddingsTest extends RateLimitAwareTest {

    private static final String INPUT = "hello";
    private static final String INPUT_2 = "hi";

    private final OpenAiClient client = OpenAiClient.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        List<Float> embedding = client.embedding("hello").execute();

        assertThat(embedding).hasSize(1536);
    }

    @MethodSource
    @ParameterizedTest
    void testCustomizableApi(EmbeddingRequest request) {

        EmbeddingResponse response = client.embedding(request).execute();


        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).embedding()).hasSize(1536);

        assertThat(response.embedding()).hasSize(1536);
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

    @ParameterizedTest
    @EnumSource(EmbeddingModel.class)
    void testCustomizableApiSingleEmbedding(EmbeddingModel model) {

        // given
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(model)
                .input(INPUT)
                .build();

        // when
        EmbeddingResponse response = client.embedding(request).execute();

        // then
        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).embedding()).isNotEmpty();

        assertThat(response.embedding()).isNotEmpty();
    }

    @ParameterizedTest
    @EnumSource(EmbeddingModel.class)
    void testCustomizableApiMultipleEmbeddings(EmbeddingModel model) {

        // given
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(model)
                .input(INPUT, INPUT_2)
                .build();

        // when
        EmbeddingResponse response = client.embedding(request).execute();

        // then
        assertThat(response.data()).hasSize(2);
        assertThat(response.data().get(0).embedding()).isNotEmpty();
        assertThat(response.data().get(1).embedding()).isNotEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = EmbeddingModel.class, mode = EXCLUDE, names = {"TEXT_EMBEDDING_ADA_002"})
    void testShortenedDimensions(EmbeddingModel model) {

        // given
        int dimensions = 128;

        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(model)
                .input(INPUT)
                .dimensions(dimensions)
                .build();

        // when
        EmbeddingResponse response = client.embedding(request).execute();

        // then
        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).embedding()).hasSize(dimensions);

        assertThat(response.embedding()).hasSize(dimensions);
    }
}
