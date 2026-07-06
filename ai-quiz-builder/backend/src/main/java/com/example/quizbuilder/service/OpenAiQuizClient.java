package com.example.quizbuilder.service;

import com.example.quizbuilder.model.QuizRequest;
import com.example.quizbuilder.model.QuizResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OpenAiQuizClient {
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public OpenAiQuizClient(
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder,
            @Value("${app.ai.openai.api-key}") String apiKey,
            @Value("${app.ai.openai.model}") String model,
            @Value("${app.ai.openai.base-url}") String baseUrl
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<QuizResponse> generate(QuizRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }

        try {
            Map<String, Object> payload = Map.of(
                    "model", model,
                    "temperature", 0.5,
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", "You generate valid JSON only. No markdown, no prose outside JSON."
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", prompt(request)
                            )
                    )
            );

            String response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(25))
                    .block();

            if (response == null || response.isBlank()) {
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").path(0).path("message").path("content").asText();
            if (content.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(content, QuizResponse.class));
        } catch (RuntimeException | JsonProcessingException ex) {
            return Optional.empty();
        }
    }

    private String prompt(QuizRequest request) {
        return """
                Create a multiple-choice quiz as JSON.

                Required JSON shape:
                {
                  "title": "string",
                  "topic": "string",
                  "difficulty": "string",
                  "questions": [
                    {
                      "prompt": "string",
                      "options": ["string", "string", "string", "string"],
                      "answerIndex": 0,
                      "explanation": "string"
                    }
                  ]
                }

                Topic: %s
                Difficulty: %s
                Question count: %d

                Rules:
                - Return exactly %d questions.
                - Each question must have exactly four options.
                - answerIndex must be 0, 1, 2, or 3.
                - Explanations should be concise and educational.
                """.formatted(
                request.topic(),
                request.difficulty(),
                request.questionCount(),
                request.questionCount()
        );
    }
}

