package dev.ai4j.openai4j.chat;

import java.util.Objects;

public final class Content {

    private final String type;
    private final String text;
    private final ImageUrl imageUrl;

    public Content(Builder builder) {
        this.type = builder.type;
        this.text = builder.text;
        this.imageUrl = builder.imageUrl;
    }

    public String type() {
        return type;
    }

    public String text() {
        return text;
    }

    public ImageUrl imageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(type, content.type) && Objects.equals(text, content.text) && Objects.equals(imageUrl, content.imageUrl);
    }

    private boolean equalTo(Content another) {
        return Objects.equals(type, another.type)
                && Objects.equals(text, another.text)
                && Objects.equals(imageUrl, another.imageUrl);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(type);
        h += (h << 5) + Objects.hashCode(text);
        h += (h << 5) + Objects.hashCode(imageUrl);
        return h;
    }

    @Override
    public String toString() {
        return "Content{" +
                "type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", imageUrl=" + imageUrl +
                '}';
    }

    public static Content.Builder builder() {
        return new Content.Builder();
    }

    public static final class Builder {
        private String type;
        private String text;
        private ImageUrl imageUrl;

        public Content.Builder type(String type) {
            this.type = type;
            return this;
        }

        public Content.Builder text(String text) {
            this.text = text;
            return this;
        }

        public Content.Builder imageUrl(ImageUrl imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Content build() {
            return new Content(this);
        }

    }


}
