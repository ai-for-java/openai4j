package dev.ai4j.openai4j.image;

/**
 * Represents an OpenAI DALL·E models to generate artistic images.
 */
public enum ImageModel {
  DALL_E_2("dall-e-2"), // anyone still needs that? :)
  DALL_E_3("dall-e-3");

  public static final String DALL_E_SIZE_256_x_256 = "256x256"; // for 2 only
  public static final String DALL_E_SIZE_512_x_512 = "512x512"; // for 2 only
  public static final String DALL_E_SIZE_1024_x_1024 = "1024x1024"; // for 2 & 3
  public static final String DALL_E_SIZE_1792_x_1024 = "1792x1024"; // for 3 only
  public static final String DALL_E_SIZE_1024_x_1792 = "1024x1792"; // for 3 only
  public static final String DALL_E_QUALITY_STANDARD = "standard";
  public static final String DALL_E_QUALITY_HD = "hd";
  public static final String DALL_E_STYLE_VIVID = "vivid";
  public static final String DALL_E_STYLE_NATURAL = "natural";

  public static final String DALL_E_RESPONSE_FORMAT_URL = "url";
  public static final String DALL_E_RESPONSE_FORMAT_B64_JSON = "b64_json";

  private final String value;

  ImageModel(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
