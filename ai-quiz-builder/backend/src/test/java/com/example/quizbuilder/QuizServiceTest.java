package com.example.quizbuilder;

import com.example.quizbuilder.model.QuizRequest;
import com.example.quizbuilder.model.QuizResponse;
import com.example.quizbuilder.service.OpenAiQuizClient;
import com.example.quizbuilder.service.QuizService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuizServiceTest {
    @Test
    void generatesFallbackQuizWhenAiClientReturnsEmpty() {
        OpenAiQuizClient client = mock(OpenAiQuizClient.class);
        QuizRequest request = new QuizRequest("Spring Boot", "medium", 3);
        when(client.generate(request)).thenReturn(Optional.empty());

        QuizResponse response = new QuizService(client).generateQuiz(request);

        assertThat(response.topic()).isEqualTo("Spring Boot");
        assertThat(response.questions()).hasSize(3);
        assertThat(response.questions().get(0).options()).hasSize(4);
    }
}

