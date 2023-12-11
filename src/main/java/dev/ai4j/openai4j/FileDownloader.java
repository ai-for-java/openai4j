package dev.ai4j.openai4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

class FileDownloader {

  static Path download(URI uri, Path destinationFolder) {
    try {
      Path fileName = Paths.get(uri.getPath()).getFileName();
      Path destinationFilePath = destinationFolder.resolve(fileName);
      try (InputStream inputStream = new URL(uri.toString()).openStream()) {
        java.nio.file.Files.copy(
          inputStream,
          destinationFilePath,
          StandardCopyOption.REPLACE_EXISTING
        );
      }

      return destinationFilePath;
    } catch (IOException e) {
      throw new RuntimeException("Error downloading file from URI: " + uri, e);
    }
  }
}
