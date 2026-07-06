package com.example.quizbuilder.controller;

import com.example.quizbuilder.model.QuizRequest;
import com.example.quizbuilder.model.QuizResponse;
import com.example.quizbuilder.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/quizzes/generate")
    public ResponseEntity<QuizResponse> generate(@Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.generateQuiz(request));
    }
}

