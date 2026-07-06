package com.example.quizbuilder.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record Question(
        @NotBlank String prompt,
        @Size(min = 4, max = 4) List<String> options,
        @Min(0) @Max(3) int answerIndex,
        @NotBlank String explanation
) {
}

