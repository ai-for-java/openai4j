package dev.ai4j.openai4j.image;

import java.util.List;

public class ImageResponse {

  private final long created;
  private final List<ImageData> data;

  private ImageResponse(Builder builder) {
    this.created = builder.created;
    this.data = builder.data;
  }

  public static Builder builder() {
    return new Builder();
  }

  public long created() {
    return created;
  }

  public List<ImageData> data() {
    return data;
  }

  public static class ImageData {

    private String url;

    private ImageData(String url) {
      this.url = url;
    }

    public String url() {
      return url;
    }

    public void url(String url) {
      this.url = url;
    }
  }

  public static class Builder {

    private long created;
    private List<ImageData> data;

    public Builder created(long created) {
      this.created = created;
      return this;
    }

    public Builder data(List<ImageData> data) {
      this.data = data;
      return this;
    }

    public ImageResponse build() {
      return new ImageResponse(this);
    }
  }
}
