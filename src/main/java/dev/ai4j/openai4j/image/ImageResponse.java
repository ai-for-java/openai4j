package dev.ai4j.openai4j.image;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object another) {
      if (this == another) return true;
      if (another == null || getClass() != another.getClass()) return false;
      return equalTo((ImageData) another);
    }

    private boolean equalTo(ImageData another) {
      return Objects.equals(url, another.url);
    }

    @Override
    public int hashCode() {
      return Objects.hash(url);
    }
  }

  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    if (another == null || getClass() != another.getClass()) return false;
    return equalTo((ImageResponse) another);
  }

  private boolean equalTo(ImageResponse another) {
    return created == another.created && Objects.equals(data, another.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, data);
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
