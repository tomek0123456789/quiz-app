package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;

public record QuizResultsPatchDto(
        long quizResultsId,
        Long quizId,
        Long score
) {
}
