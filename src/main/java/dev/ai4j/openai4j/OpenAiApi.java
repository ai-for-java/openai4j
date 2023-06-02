package dev.ai4j.openai4j;

import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import dev.ai4j.openai4j.moderation.ModerationRequest;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

interface OpenAiApi {

    @POST("v1/completions")
    @Headers("Content-Type: application/json")
    Call<CompletionResponse> completions(@Body CompletionRequest request);

    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    Call<ChatCompletionResponse> chatCompletions(@Body ChatCompletionRequest request);

    @POST("v1/embeddings")
    @Headers("Content-Type: application/json")
    Call<EmbeddingResponse> embeddings(@Body EmbeddingRequest request);

    @POST("v1/moderations")
    @Headers("Content-Type: application/json")
    Call<ModerationResponse> moderations(@Body ModerationRequest request);
}
