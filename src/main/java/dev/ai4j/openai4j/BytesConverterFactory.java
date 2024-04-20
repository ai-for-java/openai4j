package dev.ai4j.openai4j;

import dev.ai4j.openai4j.audio.GenerateSpeechResponse;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A converter factory to handle the conversion of Retrofit response bodies into
 * ByteArrayObjectWrapper instances.
 */
public class BytesConverterFactory extends Converter.Factory {

    private static final Logger logger = LoggerFactory.getLogger(BytesConverterFactory.class);

    public BytesConverterFactory() {
        // Constructor can be utilized for initializing if needed
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        logger.debug("Requesting conversion for type: {}", type.getTypeName());
        if (GenerateSpeechResponse.class.equals(type)) {
            return responseBody -> {
                try {
                    logger.debug("Converting response body to GenerateSpeechResponse");
                    return GenerateSpeechResponse.builder()
                            .data(responseBody.bytes())
                            .build();
                } catch (IOException e) {
                    logger.error("Failed to read bytes from response body", e);
                    throw new RuntimeException("Error reading response body", e);
                } finally {
                    responseBody.close();
                }
            };
        }
        logger.debug("No converter found for type: {}", type.getTypeName());
        return null;
    }
}
