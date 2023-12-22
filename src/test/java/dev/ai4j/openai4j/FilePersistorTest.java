package dev.ai4j.openai4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class FilePersistorTest {

    private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"));

    @Test
    void shouldDownloadFileAndReturnFilePath() throws URISyntaxException {
        String fileUrl = "https://www.wikipedia.org/portal/wikipedia.org/assets/img/Wikipedia-logo-v2.png";
        Path filePath = FilePersistor.persistFromUri(new URI(fileUrl), TEMP_DIR);

        assertThat(filePath).isNotNull();
        assertThat(filePath).startsWith(TEMP_DIR);
        assertThat(filePath).exists();
    }

    @Test
    void shouldThrowExceptionOnInvalidUrl() {
        assertThatThrownBy(() -> FilePersistor.persistFromUri(new URI("invalid_url"), Paths.get("destination")))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void shouldDownloadFileWithCorrectContent() {

        URI uri = URI.create("https://raw.githubusercontent.com/langchain4j/langchain4j/main/langchain4j/src/test/resources/test-file-utf8.txt");

        Path filePath = FilePersistor.persistFromUri(uri, TEMP_DIR);

        assertThat(filePath).isNotNull();
        assertThat(filePath).startsWith(TEMP_DIR);
        assertThat(filePath).exists().hasContent("test\ncontent\n");
    }

    @Test
    void shouldPersistFromBase64String() throws IOException {
        String base64EncodedString = "SGVsbG8gd29ybGQh"; // Sample "Hello world!" in base64

        Path filePath = FilePersistor.persistFromBase64String(base64EncodedString, TEMP_DIR);

        assertThat(filePath).isNotNull();
        assertThat(filePath).startsWith(TEMP_DIR);
        assertThat(filePath).exists().hasContent("Hello world!");
    }
}
