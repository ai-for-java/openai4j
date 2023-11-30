package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class ImageUrl {

    private final String url;
    private final ImageDetail detail;

    private ImageUrl(Builder builder) {
        this.url = builder.url;
        this.detail = builder.detail;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ImageUrl
                && equalTo((ImageUrl) another);
    }

    private boolean equalTo(ImageUrl another) {
        return Objects.equals(url, another.url)
                && Objects.equals(detail, another.detail);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(url);
        h += (h << 5) + Objects.hashCode(detail);
        return h;
    }

    @Override
    public String toString() {
        return "ImageUrl{" +
                "url=" + url +
                ", detail=" + detail +
                "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String url;
        private ImageDetail detail;

        private Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder detail(ImageDetail detail) {
            this.detail = detail;
            return this;
        }

        public ImageUrl build() {
            return new ImageUrl(this);
        }
    }
}
