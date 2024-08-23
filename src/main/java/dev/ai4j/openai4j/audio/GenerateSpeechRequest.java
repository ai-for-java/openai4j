package dev.ai4j.openai4j.audio;

import java.util.Objects;

/**
 * This class represents a request to generate audio speech using specific parameters.
 */
public class GenerateSpeechRequest {
    private final String model;
    private final String input;
    private final String voice;
    private final String responseFormat;
    private final double speed;

    private GenerateSpeechRequest(Builder builder) {
        this.model = Objects.requireNonNull(builder.model, "Model cannot be null");
        this.input = Objects.requireNonNull(builder.input, "Input cannot be null");
        this.voice = Objects.requireNonNull(builder.voice, "Voice cannot be null");
        this.responseFormat = builder.responseFormat;
        this.speed = builder.speed;
    }

    // Implementing equals method to ensure correct behavior in collections and other use cases.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerateSpeechRequest that = (GenerateSpeechRequest) o;
        return Double.compare(that.speed, speed) == 0 &&
                Objects.equals(model, that.model) &&
                Objects.equals(input, that.input) &&
                Objects.equals(voice, that.voice) &&
                Objects.equals(responseFormat, that.responseFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, input, voice, responseFormat, speed);
    }

    @Override
    public String toString() {
        return String.format(
                "GenerateAudioRequest{model='%s', input='%s', voice='%s', responseFormat='%s', speed=%.1f}",
                model, input, voice, responseFormat, speed
        );
    }

    // Static factory method for the builder, improving code readability.
    public static Builder builder() {
        return new Builder();
    }

    // Builder class for GenerateAudioRequest.
    public static class Builder {
        private String model;
        private String input;
        private String voice;
        private String responseFormat = SpeechModel.ResponseFormat.MP3.toString(); // Default response format
        private double speed = 1.0; // Default speed

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder model(SpeechModel model) {
            this.model = model.toString();
            return this;
        }

        public Builder input(String input) {
            this.input = input;
            return this;
        }

        public Builder voice(String voice) {
            this.voice = voice;
            return this;
        }

        public Builder voice(SpeechModel.Voice voice) {
            this.voice = voice.toString();
            return this;
        }

        public Builder responseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public Builder responseFormat(SpeechModel.ResponseFormat responseFormat) {
            this.responseFormat = responseFormat.toString();
            return this;
        }

        public Builder speed(double speed) {
            if (speed <= 0) {
                throw new IllegalArgumentException("Speed must be positive");
            }
            this.speed = speed;
            return this;
        }

        public GenerateSpeechRequest build() {
            return new GenerateSpeechRequest(this);
        }
    }
}
