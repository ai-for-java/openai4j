package dev.ai4j.openai4j.audio;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the response from the OpenAI Speech API when generating audio.
 * For a detailed description of parameters, see <a href="https://platform.openai.com/docs/api-reference/audio/createSpeech">OpenAI Speech Audio API</a>.
 */
public class GenerateSpeechResponse {

    private final byte[] data;

    private URI url;

    public GenerateSpeechResponse(Builder builder) {
        this.data = builder.data;
        this.url = builder.url;
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] data() {
        return data;
    }

    public URI url() {
        return url;
    }

    public void url(URI url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return (
                "GenerateSpeechResponse{" +
                        "url='" +
                        url +
                        '\'' +
                        ", data='" +
                        Arrays.toString(data) +
                        '}'
        );
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null || getClass() != another.getClass()) return false;
        GenerateSpeechResponse anotherGenerateSpeechResponse = (GenerateSpeechResponse) another;
        return Arrays.equals(data, anotherGenerateSpeechResponse.data)
                && Objects.equals(url, anotherGenerateSpeechResponse.url);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(url);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    public static class Builder {

        private byte[] data;
        private URI url;

        public Builder data(byte[] data) {
            this.data = data;
            return this;
        }

        public Builder url(URI url) {
            this.url = url;
            return this;
        }

        public GenerateSpeechResponse build() {
            return new GenerateSpeechResponse(this);
        }
    }
}
