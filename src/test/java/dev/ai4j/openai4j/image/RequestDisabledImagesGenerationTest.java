package dev.ai4j.openai4j.image;

import static dev.ai4j.openai4j.image.ImageModel.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

public class RequestDisabledImagesGenerationTest {

    @Test
    void generationShouldWork() {
        OpenAiClient client = OpenAiClient
            .builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

        GenerateImagesRequest request = GenerateImagesRequest
            .builder()
            .model(DALL_E_2) // so that you pay not much :)
            .size(DALL_E_SIZE_256_x_256)
            .prompt("Beautiful house on country side")
            .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.imagesGeneration(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void generationWithDownloadShouldWork() {
        OpenAiClient client = OpenAiClient
            .builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .withPersisting()
            .build();

        GenerateImagesRequest request = GenerateImagesRequest
            .builder()
            .model("dall-e-2") // so that you pay not much :)
            .size(DALL_E_SIZE_256_x_256)
            .prompt("Bird flying in the sky")
            .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.imagesGeneration(request).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void shouldPersistImageFromBase64Json() {
        OpenAiClient client = OpenAiClient
            .builder()
            .disableRequests()
            .withPersisting()
            .logRequests()
            .logResponses()
            .build();

        GenerateImagesRequest request = GenerateImagesRequest
            .builder()
            .model(DALL_E_2) // so that you pay not much :)
            .size(DALL_E_SIZE_256_x_256)
            .responseFormat(DALL_E_RESPONSE_FORMAT_B64_JSON)
            .prompt("Beautiful house on country side")
            .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.imagesGeneration(request).execute())
            .withMessage("Requests to the live system are disabled");
    }
}
