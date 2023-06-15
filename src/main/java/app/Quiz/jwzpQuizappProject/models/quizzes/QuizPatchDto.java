package app.Quiz.jwzpQuizappProject.models.quizzes;

import jakarta.persistence.Enumerated;

public record QuizPatchDto(
        String title,
        String description,
        Long categoryId,
        QuizStatus status
) {
}
