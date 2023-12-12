package dev.ai4j.openai4j.image;

import java.util.List;
import java.util.Objects;

/**
 * Represents the response from the OpenAI API when generating images.
 *
 * @param data A list of image data, each containing an image URL, Base64 encoded JSON response, and revised prompt
 */
public class GenerateImagesResponse {

  private final List<ImageData> data;

  public GenerateImagesResponse(Builder builder) {
    this.data = builder.data;
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<ImageData> data() {
    return data;
  }

  public static class ImageData {

    private String url;
    private final String b64Json;
    private final String revisedPrompt;

    public ImageData(String url, String b64Json, String revisedPrompt) {
      this.url = url;
      this.b64Json = b64Json;
      this.revisedPrompt = revisedPrompt;
    }

    public String url() {
      return url;
    }

    public String b64Json() {
      return b64Json;
    }

    public String revisedPrompt() {
      return revisedPrompt;
    }

    public void url(String url) {
      this.url = url;
    }

    @Override
    public boolean equals(Object another) {
      if (this == another) return true;
      if (another == null || getClass() != another.getClass()) return false;
      ImageData anotherImageData = (ImageData) another;
      return (
        Objects.equals(url, anotherImageData.url) &&
        Objects.equals(b64Json, anotherImageData.b64Json) &&
        Objects.equals(revisedPrompt, anotherImageData.revisedPrompt)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(url, b64Json, revisedPrompt);
    }

    @Override
    public String toString() {
      return (
        "ImageData{" +
        "url='" +
        url +
        '\'' +
        ", b64Json='" +
        b64Json +
        '\'' +
        ", revisedPrompt='" +
        revisedPrompt +
        '\'' +
        '}'
      );
    }
  }

  @Override
  public String toString() {
    return "GenerateImagesResponse{" + "data=" + data + '}';
  }

  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    if (another == null || getClass() != another.getClass()) return false;
    GenerateImagesResponse anotherGenerateImagesResponse =
      (GenerateImagesResponse) another;
    return Objects.equals(data, anotherGenerateImagesResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  public static class Builder {

    private List<ImageData> data;

    public Builder data(List<ImageData> data) {
      this.data = data;
      return this;
    }

    public GenerateImagesResponse build() {
      return new GenerateImagesResponse(this);
    }
  }
}
