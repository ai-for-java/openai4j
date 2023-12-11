package dev.ai4j.openai4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class FileDownloaderTest {

  @Test
  void shouldDownloadFileAndReturnFilePath() throws URISyntaxException {
    String fileUrl =
      "https://www.wikipedia.org/portal/wikipedia.org/assets/img/Wikipedia-logo-v2.png";
    Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
    Path filePath = FileDownloader.download(new URI(fileUrl), tempDir);

    assertThat(filePath).isNotNull();
    assertThat(filePath).startsWith(tempDir);
    assertThat(filePath).exists();
  }

  @Test
  void shouldThrowExceptionOnInvalidUrl() {
    assertThatThrownBy(() ->
        FileDownloader.download(
          new URI("invalid_url"),
          Paths.get("destination")
        )
      )
      .isInstanceOf(RuntimeException.class)
      .hasCauseInstanceOf(IOException.class);
  }

  @Test
  public void shouldDownloadFileWithCorrectContent()
    throws IOException, URISyntaxException {
    String content = "Test content";
    Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
    Path tempFile = Files.createTempFile("testfile", ".txt");
    Files.write(tempFile, content.getBytes());

    Path filePath = FileDownloader.download(tempFile.toUri(), tempDir);

    assertThat(filePath).isNotNull();
    assertThat(filePath).startsWith(tempDir);
    assertThat(filePath).exists().hasContent(content);
  }
}
