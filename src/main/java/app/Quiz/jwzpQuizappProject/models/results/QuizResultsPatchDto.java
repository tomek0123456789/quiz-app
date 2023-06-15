package app.Quiz.jwzpQuizappProject.models.results;

public record QuizResultsPatchDto(
        long quizResultsId,
        Long quizId,
        Long score
) {
}
