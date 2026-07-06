package com.example.quizbuilder.service;

import com.example.quizbuilder.model.Question;
import com.example.quizbuilder.model.QuizRequest;
import com.example.quizbuilder.model.QuizResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {
    private final OpenAiQuizClient openAiQuizClient;

    public QuizService(OpenAiQuizClient openAiQuizClient) {
        this.openAiQuizClient = openAiQuizClient;
    }

    public QuizResponse generateQuiz(QuizRequest request) {
        return openAiQuizClient.generate(request)
                .filter(quiz -> quiz.questions() != null && !quiz.questions().isEmpty())
                .orElseGet(() -> fallbackQuiz(request));
    }

    private QuizResponse fallbackQuiz(QuizRequest request) {
        List<Question> questions = new ArrayList<>();
        for (int index = 1; index <= request.questionCount(); index++) {
            questions.add(new Question(
                    "What is an important " + request.difficulty().toLowerCase() + " concept in " + request.topic() + "?",
                    List.of(
                            "A core idea that helps explain " + request.topic(),
                            "A random fact unrelated to the topic",
                            "A deprecated term with no current use",
                            "A formatting choice only"
                    ),
                    0,
                    "The correct answer focuses on a meaningful concept from " + request.topic() + ". Add an AI key for richer generated explanations."
            ));
        }

        return new QuizResponse(
                request.topic() + " Quiz",
                request.topic(),
                request.difficulty(),
                questions
        );
    }
}

