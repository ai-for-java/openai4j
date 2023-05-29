# (Unofficial) Java Client for OpenAI

## Current capabilities:
- Completions
  - synchronous
  - asynchronous
  - streaming
- Chat Completions
  - synchronous
  - asynchronous
  - streaming
- Embeddings
  - synchronous
  - asynchronous

## Start using
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

## How to use
Create service (easy):
```
String apiKey = System.getenv("OPENAI_API_KEY");

OpenAiService openAiService = new OpenAiService();
```
Create service (flexible):
```
String apiKey = System.getenv("OPENAI_API_KEY");

OpenAiService openAiServiceMy = OpenAiService.builder()
    .apiKey(apiKey)
    .timeout(Duration.ofSeconds(60))
    .build();
```

Get completions:
```
CompletionRequest request = CompletionRequest.builder()
	  .model(GPT_3_5_TURBO)
	  .prompt("Write a poem about ChatGpt")
	  .temperature(0.9)
	  .build();

CompletionResponse response = openAiService.getCompletions(request);
```
