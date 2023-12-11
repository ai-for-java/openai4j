package dev.ai4j.openai4j.image;

import static dev.ai4j.openai4j.image.ImageModel.DALL_E_2;
import static dev.ai4j.openai4j.image.ImageModel.DALL_E_SIZE_256_x_256;
import static org.assertj.core.api.Assertions.assertThat;

import dev.ai4j.openai4j.OpenAiClient;
import java.io.File;
import org.junit.jupiter.api.Test;

public class ImagesGenerationsTest {

  @Test
  void generationShouldWork() {
    OpenAiClient client = OpenAiClient
      .builder()
      .openAiApiKey(System.getenv("OPENAI_API_KEY"))
      .logRequests()
      .logResponses()
      .build();

    GenerateImagesRequest request = GenerateImagesRequest
      .builder()
      .model(DALL_E_2) // so that you pay not much :)
      .size(DALL_E_SIZE_256_x_256)
      .prompt("Beautiful house on country side")
      .build();

    GenerateImagesResponse response = client.imagesGeneration(request).execute();

    String remoteImage = response.data().get(0).url();

    System.out.println("Your remote image is here: " + remoteImage);

    assertThat(response.data()).hasSize(1);
    assertThat(response.data().get(0).url()).isNotNull();
  }

  @Test
  void generationWithDownloadShouldWork() {
    OpenAiClient client = OpenAiClient
      .builder()
      .openAiApiKey(System.getenv("OPENAI_API_KEY"))
      .logRequests()
      .logResponses()
      .withDownload()
      .build();

    GenerateImagesRequest request = GenerateImagesRequest
      .builder()
      .model(DALL_E_2) // so that you pay not much :)
      .size(DALL_E_SIZE_256_x_256)
      .prompt("Bird flying in the sky")
      .build();

    GenerateImagesResponse response = client.imagesGeneration(request).execute();

    String localImage = response.data().get(0).url();

    System.out.println("Your local image is here: " + localImage);

    assertThat(new File(localImage)).exists();
  }
}
