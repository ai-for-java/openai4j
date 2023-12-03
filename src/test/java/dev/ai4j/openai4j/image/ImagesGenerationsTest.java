package dev.ai4j.openai4j.image;

import static dev.ai4j.openai4j.image.ImageModel.DALL_E_2;
import static dev.ai4j.openai4j.image.ImageModel.DALL_E_SIZE_256_x_256;
import static org.assertj.core.api.Assertions.assertThat;

import dev.ai4j.openai4j.OpenAiClient;
import org.junit.jupiter.api.Test;

public class ImagesGenerationsTest {

  private final OpenAiClient client = OpenAiClient
    .builder()
    .openAiApiKey(System.getenv("OPENAI_API_KEY"))
    .logRequests()
    .logResponses()
    .build();

  @Test
  void generationShouldWork() {
    ImageRequest request = ImageRequest
      .builder()
      .model(DALL_E_2)
      .size(DALL_E_SIZE_256_x_256)
      .prompt("Beautiful house on country side")
      .build();

    ImageResponse response = client.imagesGenerations(request).execute();

    assertThat(response.data()).hasSize(1);
    assertThat(response.data().get(0).url()).isNotNull();
  }
  //  @Test
  //  void generationWithDownloadShouldWork() {
  //    ImageRequest request = ImageRequest
  //      .builder()
  //      .model(DALL_E_2)
  //      .size(DALL_E_SIZE_256_x_256)
  //      .prompt("Beautiful house on country side")
  //      .build();
  //
  //    ImageResponse response = client.imagesGenerations(request).execute();
  //
  //    assertThat(response.data()).hasSize(1);
  //    assertThat(response.data().get(0).url()).isNotNull();
  //  }
}
