package dev.ai4j.openai4j.audio;

import dev.ai4j.openai4j.OpenAiClient;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

import static dev.ai4j.openai4j.audio.SpeechModel.TTS_1;
import static org.assertj.core.api.Assertions.assertThat;

public class SpeechGenerationTest {

    @Test
    void generationShouldWork() {
        OpenAiClient client = OpenAiClient
            .builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

        GenerateSpeechRequest request = GenerateSpeechRequest
            .builder()
            .model(TTS_1)
            .voice(SpeechModel.Voice.ALLOY)
            .input("Beautiful house on country side")
            .build();

        GenerateSpeechResponse response = client.speechGeneration(request).execute();

        byte[] audioSpeechData = response.data();

        System.out.println("Your audio speech is here: " + Arrays.toString(audioSpeechData));

        assertThat(response.data()).isNotEmpty();
    }

    @Test
    void generationWithDownloadShouldWork() {
        OpenAiClient client = OpenAiClient
            .builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .withPersisting()
            .build();

        GenerateSpeechRequest request = GenerateSpeechRequest
            .builder()
            .model(TTS_1)
            .voice(SpeechModel.Voice.ALLOY)
            .input("Bird flying in the sky")
            .build();

        GenerateSpeechResponse response = client.speechGeneration(request).execute();

        URI speechUrl = response.url();

        System.out.println("Your audio speech url is here: " + speechUrl);

        assertThat(new File(speechUrl)).exists();
    }
}
