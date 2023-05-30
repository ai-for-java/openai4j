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
    <version>0.1.0</version>
</dependency>
```

Gradle:
```
implementation 'dev.ai4j:openai4j:0.1.0'
```

# Create an OpenAI Service

Easy way:
```
String apiKey = System.getenv("OPENAI_API_KEY");

OpenAiService service = new OpenAiService(apiKey);
```

Customizable way:
```
String apiKey = System.getenv("OPENAI_API_KEY");

OpenAiService service = OpenAiService.builder()
	.apiKey(apiKey)
	.timeout(ofSeconds(60))
	// other customizations coming soon!
	.build();
```

# Get Completions

## Synchronously

Easy way:
```
String completion = service.getCompletion("Write a poem about ChatGPT");
```

Customizable way:
```
CompletionRequest request = CompletionRequest.builder()
	.model(TEXT_DAVINCI_003)
	.prompt("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

CompletionResponse response = service.getCompletions(request);
```

## Asynchronously

Easy way:
```
service.getCompletionAsync("Write a poem about ChatGPT", new ResponseHandler<String>() {

	@Override
	public void onResponse(String response) {

	}

	@Override
	public void onFailure(Throwable t) {

	}
});
```

Customizable way:
```
CompletionRequest request = CompletionRequest.builder()
	.model(TEXT_DAVINCI_003)
	.prompt("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

service.getCompletionsAsync(request, new ResponseHandler<CompletionResponse>() {

	@Override
	public void onResponse(CompletionResponse response) {

	}

	@Override
	public void onFailure(Throwable t) {

	}
});
```

## Streaming

Easy way:
```
service.streamCompletion("Write a poem about ChatGPT", new StreamingResponseHandler() {

	@Override
	public void onPartialResponse(String partialResponse) {

	}

	@Override
	public void onCompleteResponse(String completeResponse) {

	}
	
	@Override
	public void onFailure(Throwable t) {

	}
});
```

Customizable way:
```
CompletionRequest request = CompletionRequest.builder()
	.model(TEXT_DAVINCI_003)
	.prompt("Write a poem about ChatGPT")
	.temperature(0.9)
	...
	.build();

service.streamCompletions(request, new StreamingResponseHandler() {

	@Override
	public void onPartialResponse(String partialResponse) {

	}

	@Override
	public void onCompleteResponse(String completeResponse) {

	}
	
	@Override
	public void onFailure(Throwable t) {

	}
});
```

# Get Chat Completions

## Synchronously

Easy way:
```
String completion = service.getChatCompletion("Write a poem about ChatGPT");
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

ChatCompletionResponse response = service.getChatCompletions(request);
```

## Asynchronously

Easy way:
```
service.getChatCompletionAsync("Write a poem about ChatGPT", new ResponseHandler<String>() {

	@Override
	public void onResponse(String response) {

	}

	@Override
	public void onFailure(Throwable t) {

	}
});
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

service.getChatCompletionsAsync(request, new ResponseHandler<ChatCompletionResponse>() {

	@Override
	public void onResponse(ChatCompletionResponse response) {

	}

	@Override
	public void onFailure(Throwable t) {

	}
});
```

## Streaming

Easy way:
```
service.streamChatCompletion("Write a poem about ChatGPT", new StreamingResponseHandler() {

	@Override
	public void onPartialResponse(String partialResponse) {

	}

	@Override
	public void onCompleteResponse(String completeResponse) {

	}
	
	@Override
	public void onFailure(Throwable t) {

	}
});
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

service.streamChatCompletions(request, new StreamingResponseHandler() {

	@Override
	public void onPartialResponse(String partialResponse) {

	}

	@Override
	public void onCompleteResponse(String completeResponse) {

	}
	
	@Override
	public void onFailure(Throwable t) {

	}
});
```

# Embeddings

## Synchronously

Easy way:
```
List<Float> embedding = service.getEmbedding("Write a poem about ChatGPT");
```

Customizable way:
```
EmbeddingRequest request = EmbeddingRequest.builder()
	.model(TEXT_EMBEDDING_ADA_002)
	.input("Write a poem about ChatGPT", "Write a haiku about ChatGPT")
	...
	.build();

EmbeddingResponse embedding = service.getEmbeddings(request);
```

## Asynchronously

Easy way:
```
service.getEmbeddingAsync("Write a poem about ChatGPT", new ResponseHandler<List<Float>>() {

	@Override
	public void onResponse(List<Float> embedding) {

	}

	@Override
	public void onFailure(Throwable t) {

	}
});
```

Customizable way:
```
EmbeddingRequest request = EmbeddingRequest.builder()
	.model(TEXT_EMBEDDING_ADA_002)
	.input("Write a poem about ChatGPT", "Write a haiku about ChatGPT")
	...
	.build();

service.getEmbeddingsAsync(request, new ResponseHandler<EmbeddingResponse>() {

	@Override
	public void onResponse(EmbeddingResponse response) {

	}

	@Override
	public void onFailure(Throwable t) {

	}
});
```

# Experimental

```
// simple
OpenAi openAi = new OpenAi(apiKey);

// customizable
OpenAi openAi = OpenAi.builder()
	.apiKey(apiKey)
	.timeout(ofSeconds(60))
	...
	.build();

// sync
CompletionResponse response = openAi.completion().sync(request);


// async
openAi.completion().async(request)
	.onResponse(response -> ...)
	.onFailure(failure -> ...)
	.execute();
	

// async
openAi.completion().async(request, new ResponseHandler<String>() {

	@Override
	public void onResponse(String response) {
		...
	}

	@Override
	public void onFailure(Throwable failure) {
		...
	}
});


// stream
openAi.completion().stream(request)
	.onPartialResponse(partialResponse -> ...)
	.onComplete(completeResponse -> ...)
	.onFailure(failure -> ...)
	.execute();


// stream
openAi.completion().stream(request, new StreamingResponseHandler<String>() {

	@Override
	public void onPartialResponse(String partialResponse) {
		...
	}
	
	@Override
	public void onComplete(String completeResponse) {
		...
	}

	@Override
	public void onFailure(Throwable failure) {
		...
	}
});
```
