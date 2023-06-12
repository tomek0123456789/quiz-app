package app.Quiz.jwzpQuizappProject.models.quizzes;

public record QuizPatchDto(
        String title,
        String description,
        Long categoryId
) {
}
