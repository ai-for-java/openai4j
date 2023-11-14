package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class ImageUrl {

    private final String url;

    private final String detail;

    private ImageUrl(Builder builder){
        this.url = builder.url;
        this.detail = builder.detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageUrl imageUrl = (ImageUrl) o;
        return Objects.equals(url, imageUrl.url) && Objects.equals(detail, imageUrl.detail);
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
                "url='" + url + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }

    public static ImageUrl.Builder builder() {
        return new ImageUrl.Builder();
    }


    public static final class Builder{
        private String url;

        private String detail = ImageDetail.AUTO.stringValue();

        private Builder(){

        }

        public Builder detail(ImageDetail detail){
            this.detail = detail.stringValue();
            return this;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public ImageUrl build(){
            return new ImageUrl(this);
        }
    }

}
