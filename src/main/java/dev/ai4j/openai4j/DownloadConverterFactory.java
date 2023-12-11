package dev.ai4j.openai4j;

import dev.ai4j.openai4j.image.GenerateImagesResponse;
import dev.ai4j.openai4j.shared.FileDownloader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

class DownloadConverterFactory extends Converter.Factory {

  private final String downloadTo;

  DownloadConverterFactory(String downloadTo) {
    this.downloadTo = downloadTo;
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(
    Type type,
    Annotation[] annotations,
    Retrofit retrofit
  ) {
    return new DownloadConverter<>(
      retrofit.nextResponseBodyConverter(this, type, annotations)
    );
  }

  private class DownloadConverter<T> implements Converter<ResponseBody, T> {

    private final Converter<ResponseBody, T> delegate;

    DownloadConverter(Converter<ResponseBody, T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
      T response = delegate.convert(value);

      if (response instanceof GenerateImagesResponse) {
        ((GenerateImagesResponse) response).data()
          .forEach(data ->
            data.url(FileDownloader.download(data.url(), downloadTo))
          );
      }

      return response;
    }
  }
}
