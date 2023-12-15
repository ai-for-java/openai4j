package dev.ai4j.openai4j.image;

import static dev.ai4j.openai4j.image.ImageModel.DALL_E_3;
import static dev.ai4j.openai4j.image.ImageModel.DALL_E_RESPONSE_FORMAT_URL;

import java.util.Objects;

/**
 * Represents the request from the OpenAI DALL·E API when generating images.
 * Find description of parameters <a href="https://platform.openai.com/docs/api-reference/images/create">here</a>.
 */
public class GenerateImagesRequest {

    private final String model;
    private final String prompt;
    private final int n;
    private final String size;
    private final String quality;
    private final String style;
    private final String user;
    private final String responseFormat;

    private GenerateImagesRequest(Builder builder) {
        this.model = builder.model.toString();
        this.prompt = builder.prompt;
        this.n = builder.n;
        this.size = builder.size;
        this.quality = builder.quality;
        this.style = builder.style;
        this.user = builder.user;
        this.responseFormat = builder.responseFormat;
    }

    public int hashCode() {
        int h = 2381;
        h += (h << 5) + Objects.hashCode(model);
        h += (h << 5) + Objects.hashCode(prompt);
        h += (h << 5) + n;
        h += (h << 5) + Objects.hashCode(size);
        h += (h << 5) + Objects.hashCode(quality);
        h += (h << 5) + Objects.hashCode(style);
        h += (h << 5) + Objects.hashCode(user);
        h += (h << 5) + Objects.hashCode(responseFormat);
        return h;
    }

    public String toString() {
        return (
            "GenerateImagesRequest{" +
            "model=" +
            model +
            ", prompt=" +
            prompt +
            ", n=" +
            n +
            ", size=" +
            size +
            ", quality=" +
            quality +
            ", style=" +
            style +
            ", user=" +
            user +
            ", responseFormat=" +
            responseFormat +
            '}'
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ImageModel model = DALL_E_3;
        private String prompt;
        private int n = 1;
        private String size;
        private String quality;
        private String style;
        private String user;
        private String responseFormat = DALL_E_RESPONSE_FORMAT_URL;

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

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder responseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public GenerateImagesRequest build() {
            return new GenerateImagesRequest(this);
        }
    }
}
