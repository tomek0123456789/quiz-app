package app.Quiz.jwzpQuizappProject.models.quizzes;

import org.springframework.lang.NonNull;

public record QuizDto(
        @NonNull String name,
        @NonNull String description,
        long categoryId
) {
}
