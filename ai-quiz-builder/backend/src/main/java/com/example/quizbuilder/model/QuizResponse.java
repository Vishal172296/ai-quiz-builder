package com.example.quizbuilder.model;

import java.util.List;

public record QuizResponse(
        String title,
        String topic,
        String difficulty,
        List<Question> questions
) {
}

