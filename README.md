# Java Client library for OpenAI API
This is an unofficial Java client library that helps to connect your Java applications with OpenAI API.

## Current capabilities:
- [Completions](https://platform.openai.com/docs/api-reference/completions)
  - synchronous
  - asynchronous
  - streaming
- [Chat Completions](https://platform.openai.com/docs/api-reference/chat)
  - synchronous
  - asynchronous
  - streaming
- [Embeddings](https://platform.openai.com/docs/api-reference/embeddings)
  - synchronous
  - asynchronous
- [Moderations](https://platform.openai.com/docs/api-reference/moderations)
  - synchronous
  - asynchronous

## Coming soon:
- Detailed javadocs
- The rest of API endpoints
- [Tell us what you need](https://github.com/ai-for-java/openai4j/issues/new)

# Start using

Maven:
```
<dependency>
    <groupId>dev.ai4j</groupId>
    <artifactId>openai4j</artifactId>
    <version>0.2.0</version>
</dependency>
```

Gradle:
```
implementation 'dev.ai4j:openai4j:0.2.0'
```

# Create an OpenAI Client

Easy way:
```
String apiKey = System.getenv("OPENAI_API_KEY");

OpenAiClient client = new OpenAiClient(apiKey);
```

Customizable way:
```
String apiKey = System.getenv("OPENAI_API_KEY");

OpenAiClient client = OpenAiClient.builder()
	.apiKey(apiKey)
	.timeout(ofSeconds(60))
	.logRequests()
	.logResponses()
	// other customizations coming soon!
	.build();
```

# Get Completions

## Synchronously

Easy way:
```
String completion = client.completion("Write a poem about ChatGPT").execute();
```

Customizable way:
```
CompletionRequest request = CompletionRequest.builder()
	.model(TEXT_DAVINCI_003)
	.prompt("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

CompletionResponse response = client.completion(request).execute();
```

## Asynchronously

Easy way:
```
client.completion("Write a poem about ChatGPT")
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

Customizable way:
```
CompletionRequest request = CompletionRequest.builder()
	.model(TEXT_DAVINCI_003)
	.prompt("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

client.completion(request)
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

## Streaming

Easy way:
```
client.completion("Write a poem about ChatGPT")
	.onPartialResponse(partialResponse -> ...)
	.onComplete(() -> ...)
	.onError(error -> ...)
	.execute();
```

Customizable way:
```
CompletionRequest request = CompletionRequest.builder()
	.model(TEXT_DAVINCI_003)
	.prompt("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

client.completion(request)
	.onPartialResponse(partialResponse -> ...)
	.onComplete(() -> ...)
	.onError(error -> ...)
	.execute();
```

# Get Chat Completions

## Synchronously

Easy way:
```
String completion = client.chatCompletion("Write a poem about ChatGPT").execute();
```

Customizable way:
```
ChatCompletionRequest request = ChatCompletionRequest.builder()
	.model(GPT_3_5_TURBO)
	.addSystemMessage("You are a helpful assistant")
	.addUserMessage("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

ChatCompletionResponse response = client.chatCompletions(request).execute();
```

## Asynchronously

Easy way:
```
client.chatCompletion("Write a poem about ChatGPT")
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

Customizable way:
```
ChatCompletionRequest request = ChatCompletionRequest.builder()
	.model(GPT_3_5_TURBO)
	.addSystemMessage("You are a helpful assistant")
	.addUserMessage("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

client.chatCompletion(request)
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

## Streaming

Easy way:
```
client.chatCompletion("Write a poem about ChatGPT")
	.onPartialResponse(partialResponse -> ...)
	.onComplete(() -> ...)
	.onError(error -> ...)
	.execute();
```

Customizable way:
```
ChatCompletionRequest request = ChatCompletionRequest.builder()
	.model(GPT_3_5_TURBO)
	.addSystemMessage("You are a helpful assistant")
	.addUserMessage("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

client.chatCompletion(request)
	.onPartialResponse(partialResponse -> ...)
	.onComplete(() -> ...)
	.onError(error -> ...)
	.execute();
```

# Embeddings

## Synchronously

Easy way:
```
List<Float> embedding = client.embedding("Write a poem about ChatGPT").execute();
```

Customizable way:
```
EmbeddingRequest request = EmbeddingRequest.builder()
	.model(TEXT_EMBEDDING_ADA_002)
	.input("Write a poem about ChatGPT", "Write a haiku about ChatGPT")
	...
	.build();

EmbeddingResponse embedding = client.embedding(request).execute();
```

## Asynchronously

Easy way:
```
client.embedding("Write a poem about ChatGPT")
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

Customizable way:
```
EmbeddingRequest request = EmbeddingRequest.builder()
	.model(TEXT_EMBEDDING_ADA_002)
	.input("Write a poem about ChatGPT", "Write a haiku about ChatGPT")
	...
	.build();

client.embedding(request)
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

# Moderations

## Synchronously

Easy way:
```
ModerationResult moderationResult = client.moderation("Write a poem about ChatGPT").execute();
```

Customizable way:
```
ModerationRequest request = ModerationRequest.builder()
	.input(INPUT)
	.model(TEXT_MODERATION_STABLE)
	.build();

ModerationResponse response = client.moderation(request).execute();
```

## Asynchronously

Easy way:
```
client.moderation("Write a poem about ChatGPT")
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```

Customizable way:
```
ModerationRequest request = ModerationRequest.builder()
	.input(INPUT)
	.model(TEXT_MODERATION_STABLE)
	.build();

client.moderation(request)
	.onResponse(response -> ...)
	.onError(error -> ...)
	.execute();
```
