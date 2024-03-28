package dev.ai4j.openai4j;

import org.junit.jupiter.api.Test;

import static dev.ai4j.openai4j.RequestLoggingInterceptor.format;
import static dev.ai4j.openai4j.RequestLoggingInterceptor.maskSecretKey;
import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingInterceptorTest {

    @Test
    void should_mask_secret_headers() {

        assertThat(format("Authorization", null))
                .isEqualTo("[Authorization: null]");
        assertThat(format("Authorization", "Bearer 1234567890"))
                .isEqualTo("[Authorization: Bearer 12345...90]");
        assertThat(format("authorization", "Bearer 1234567890"))
                .isEqualTo("[authorization: Bearer 12345...90]");

        assertThat(format("x-api-key", null))
                .isEqualTo("[x-api-key: null]");
        assertThat(format("x-api-key", "1234567890"))
                .isEqualTo("[x-api-key: 12345...90]");
        assertThat(format("X-API-KEY", "1234567890"))
                .isEqualTo("[X-API-KEY: 12345...90]");

        assertThat(format("X-Auth-Token", null))
                .isEqualTo("[X-Auth-Token: null]");
        assertThat(format("X-Auth-Token", "1234567890"))
                .isEqualTo("[X-Auth-Token: 12345...90]");
        assertThat(format("x-auth-token", "1234567890"))
                .isEqualTo("[x-auth-token: 12345...90]");
    }

    @Test
    void should_mask_secret() {

        assertThat(maskSecretKey(null)).isNull();
        assertThat(maskSecretKey("")).isEqualTo("");
        assertThat(maskSecretKey(" ")).isEqualTo(" ");
        assertThat(maskSecretKey("key")).isEqualTo("...");
        assertThat(maskSecretKey("Bearer sk-1234567890")).isEqualTo("Bearer sk-12...90");
        assertThat(maskSecretKey("sk-1234567890")).isEqualTo("sk-12...90");
        assertThat(maskSecretKey("gsk_1234567890")).isEqualTo("gsk_1...90");
        assertThat(maskSecretKey("Bearer gsk_1234567890")).isEqualTo("Bearer gsk_1...90");
    }
}