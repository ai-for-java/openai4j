# Java client library for OpenAI API
This is an unofficial Java client library that helps to connect your Java applications with OpenAI API.

## Current capabilities:
- [Completions](https://platform.openai.com/docs/api-reference/completions)
  - [synchronous](https://github.com/ai-for-java/openai4j#synchronously)
  - [asynchronous](https://github.com/ai-for-java/openai4j#asynchronously)
  - [streaming](https://github.com/ai-for-java/openai4j#streaming)
- [Chat Completions](https://platform.openai.com/docs/api-reference/chat)
  - [synchronous](https://github.com/ai-for-java/openai4j#synchronously-1)
  - [asynchronous](https://github.com/ai-for-java/openai4j#asynchronously-1)
  - [streaming](https://github.com/ai-for-java/openai4j#streaming-1)
- [Embeddings](https://platform.openai.com/docs/api-reference/embeddings)
  - [synchronous](https://github.com/ai-for-java/openai4j#synchronously-2)
  - [asynchronous](https://github.com/ai-for-java/openai4j#asynchronously-2)
- [Moderations](https://platform.openai.com/docs/api-reference/moderations)
  - [synchronous](https://github.com/ai-for-java/openai4j#synchronously-3)
  - [asynchronous](https://github.com/ai-for-java/openai4j#asynchronously-3)

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

# Code examples

## Create an OpenAI Client

Simple way:
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

## Completions

### Synchronously

Simple way:
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

### Asynchronously

Simple way:
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

### Streaming

Simple way:
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

## Chat Completions

### Synchronously

Simple way:
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

### Asynchronously

Simple way:
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

### Streaming

Simple way:
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

## Embeddings

### Synchronously

Simple way:
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

### Asynchronously

Simple way:
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

## Moderations

### Synchronously

Simple way:
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

### Asynchronously

Simple way:
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
