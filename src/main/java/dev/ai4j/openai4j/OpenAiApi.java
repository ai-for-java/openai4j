package dev.ai4j.openai4j;

import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import dev.ai4j.openai4j.image.ImageRequest;
import dev.ai4j.openai4j.image.ImageResponse;
import dev.ai4j.openai4j.moderation.ModerationRequest;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface OpenAiApi {
  @POST("completions")
  @Headers("Content-Type: application/json")
  Call<CompletionResponse> completions(
    @Body CompletionRequest request,
    @Query("api-version") String apiVersion
  );

  @POST("chat/completions")
  @Headers("Content-Type: application/json")
  Call<ChatCompletionResponse> chatCompletions(
    @Body ChatCompletionRequest request,
    @Query("api-version") String apiVersion
  );

  @POST("embeddings")
  @Headers("Content-Type: application/json")
  Call<EmbeddingResponse> embeddings(
    @Body EmbeddingRequest request,
    @Query("api-version") String apiVersion
  );

  @POST("moderations")
  @Headers("Content-Type: application/json")
  Call<ModerationResponse> moderations(
    @Body ModerationRequest request,
    @Query("api-version") String apiVersion
  );

  @POST("images/generations")
  @Headers({ "Content-Type: application/json" })
  Call<ImageResponse> imagesGenerations(
    @Body ImageRequest request,
    @Query("api-version") String apiVersion
  );
}
