package dev.ai4j.openai4j.audio;

/**
 * Enum representing different OpenAI audio speech models.
 */
public enum SpeechModel {
    TTS_1("tts-1"),
    TTS_1_HD("tts-1-hd");

    private final String modelId;

    SpeechModel(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return modelId;
    }


    /**
     * Enum representing different voice types for TTS (Text To Speech).
     */
    public enum Voice {
        ALLOY("alloy"),
        ECHO("echo"),
        FABLE("fable"),
        ONYX("onyx"),
        NOVA("nova"),
        SHIMMER("shimmer");

        private final String voiceType;

        Voice(String voiceType) {
            this.voiceType = voiceType;
        }

        @Override
        public String toString() {
            return voiceType;
        }
    }

    /**
     * Enum representing different audio response formats.
     */
    public enum ResponseFormat {
        MP3("mp3"),
        OPUS("opus"),
        AAC("aac"),
        FLAC("flac"),
        WAV("wav"),
        PCM("pcm");

        private final String format;

        ResponseFormat(String format) {
            this.format = format;
        }

        @Override
        public String toString() {
            return format;
        }
    }
}

