package com.example.quizbuilder.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record QuizRequest(
        @NotBlank(message = "Topic is required")
        String topic,

        @NotBlank(message = "Difficulty is required")
        String difficulty,

        @Min(value = 1, message = "At least one question is required")
        @Max(value = 12, message = "A quiz can have at most 12 questions")
        int questionCount
) {
}

