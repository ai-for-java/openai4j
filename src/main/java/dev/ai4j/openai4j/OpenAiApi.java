package dev.ai4j.openai4j;

import dev.ai4j.openai4j.audio.GenerateSpeechRequest;
import dev.ai4j.openai4j.audio.GenerateSpeechResponse;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import dev.ai4j.openai4j.image.GenerateImagesRequest;
import dev.ai4j.openai4j.image.GenerateImagesResponse;
import dev.ai4j.openai4j.moderation.ModerationRequest;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface OpenAiApi {
    @POST("completions")
    @Headers("Content-Type: application/json")
    Call<CompletionResponse> completions(@Body CompletionRequest request,
        @Query("api-version") String apiVersion);

    @POST("completions")
    @Headers("Content-Type: application/json")
    Call<CompletionResponse> completions(
        @HeaderMap Map<String, String> headers,
        @Body CompletionRequest request,
        @Query("api-version") String apiVersion);

    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    Call<ChatCompletionResponse> chatCompletions(
        @Body ChatCompletionRequest request,
        @Query("api-version") String apiVersion
    );

    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    Call<ChatCompletionResponse> chatCompletions(
        @HeaderMap Map<String, String> headers,
        @Body ChatCompletionRequest request,
        @Query("api-version") String apiVersion
    );

    @POST("embeddings")
    @Headers("Content-Type: application/json")
    Call<EmbeddingResponse> embeddings(
        @Body EmbeddingRequest request,
        @Query("api-version") String apiVersion);

    @POST("embeddings")
    @Headers("Content-Type: application/json")
    Call<EmbeddingResponse> embeddings(
        @HeaderMap Map<String, String> headers,
        @Body EmbeddingRequest request,
        @Query("api-version") String apiVersion);

    @POST("moderations")
    @Headers("Content-Type: application/json")
    Call<ModerationResponse> moderations(
        @Body ModerationRequest request,
        @Query("api-version") String apiVersion);

    @POST("moderations")
    @Headers("Content-Type: application/json")
    Call<ModerationResponse> moderations(
        @HeaderMap Map<String, String> headers,
        @Body ModerationRequest request,
        @Query("api-version") String apiVersion);

    @POST("images/generations")
    @Headers({"Content-Type: application/json"})
    Call<GenerateImagesResponse> imagesGenerations(
        @Body GenerateImagesRequest request,
        @Query("api-version") String apiVersion
    );

    @POST("images/generations")
    @Headers({"Content-Type: application/json"})
    Call<GenerateImagesResponse> imagesGenerations(
        @HeaderMap Map<String, String> headers,
        @Body GenerateImagesRequest request,
        @Query("api-version") String apiVersion
    );

    @POST("audio/speech")
    @Headers({ "Content-Type: application/json" })
    Call<GenerateSpeechResponse> speechGenerations(
            @Body GenerateSpeechRequest request,
            @Query("api-version") String apiVersion
    );
}
