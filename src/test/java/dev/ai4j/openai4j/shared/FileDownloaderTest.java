package dev.ai4j.openai4j.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FileDownloaderTest {

  @Test
  void shouldDownloadFileAndReturnFilePath() {
    String fileUrl =
      "https://www.wikipedia.org/portal/wikipedia.org/assets/img/Wikipedia-logo-v2.png";
    String tempDir = System.getProperty("java.io.tmpdir");
    String filePath = FileDownloader.download(fileUrl, tempDir);

    assertThat(filePath).isNotNull();
    assertThat(filePath).startsWith(tempDir);
    assertThat(new File(filePath)).exists();
  }

  @Test
  void shouldThrowExceptionOnInvalidUrl() {
    String invalidUrl = "invalid_url";

    assertThatThrownBy(() -> FileDownloader.download(invalidUrl, "destination"))
      .isInstanceOf(RuntimeException.class)
      .hasCauseInstanceOf(IOException.class);
  }

  @Test
  public void shouldDownloadFileWithCorrectContent() throws IOException {
    String content = "Test content";
    String tempDir = System.getProperty("java.io.tmpdir");
    Path tempFile = Files.createTempFile("testfile", ".txt");
    Files.write(tempFile, content.getBytes());

    String fileUrl = tempFile.toUri().toString();
    String filePath = FileDownloader.download(fileUrl, tempDir);

    assertThat(filePath).isNotNull();
    assertThat(filePath).startsWith(tempDir);
    assertThat(new File(filePath)).exists();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String fileContent = reader.readLine();
      assertThat(fileContent).isEqualTo(content);
    }
  }
}
