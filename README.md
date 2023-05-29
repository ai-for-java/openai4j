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

# Create a Service

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
	.timeout(Duration.ofSeconds(60))
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

