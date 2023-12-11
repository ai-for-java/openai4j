package dev.ai4j.openai4j.image;

import static dev.ai4j.openai4j.image.ImageModel.DALL_E_3;

import java.util.Objects;

/**
 * Find description of parameters <a href="https://platform.openai.com/docs/api-reference/images/create">here</a>.
 */
public class GenerateImagesRequest {

  private final String model;
  private final String prompt;
  private final int n;
  private final String size;
  private final String quality;
  private final String style;

  private GenerateImagesRequest(Builder builder) {
    this.model = builder.model.toString();
    this.prompt = builder.prompt;
    this.n = builder.n;
    this.size = builder.size;
    this.quality = builder.quality;
    this.style = builder.style;
  }

  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof GenerateImagesRequest && equalTo((GenerateImagesRequest) another);
  }

  private boolean equalTo(GenerateImagesRequest another) {
    return (
      n == another.n &&
      Objects.equals(model, another.model) &&
      Objects.equals(prompt, another.prompt) &&
      Objects.equals(size, another.size) &&
      Objects.equals(quality, another.quality) &&
      Objects.equals(style, another.style)
    );
  }

  @Override
  public int hashCode() {
    int h = 2381;
    h += (h << 5) + Objects.hashCode(model);
    h += (h << 5) + Objects.hashCode(prompt);
    h += (h << 5) + n;
    h += (h << 5) + Objects.hashCode(size);
    h += (h << 5) + Objects.hashCode(quality);
    h += (h << 5) + Objects.hashCode(style);
    return h;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String model() {
    return model;
  }

  public String prompt() {
    return prompt;
  }

  public int n() {
    return n;
  }

  public String size() {
    return size;
  }

  public String quality() {
    return quality;
  }

  public String style() {
    return style;
  }

  public static class Builder {

    private ImageModel model = DALL_E_3;
    private String prompt;
    private int n = 1;
    private String size;
    private String quality;
    private String style;

    public Builder model(ImageModel model) {
      this.model = model;
      return this;
    }

    public Builder prompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    public Builder n(int n) {
      this.n = n;
      return this;
    }

    public Builder size(String size) {
      this.size = size;
      return this;
    }

    public Builder quality(String quality) {
      this.quality = quality;
      return this;
    }

    public Builder style(String style) {
      this.style = style;
      return this;
    }

    public GenerateImagesRequest build() {
      return new GenerateImagesRequest(this);
    }
  }
}
