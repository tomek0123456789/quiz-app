package app.Quiz.jwzpQuizappProject.models.quizzes;

import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;

import java.util.List;

public record QuizPatchDto(
        long quizId,
        String title,
        String description,
        List<QuestionModel> questions,
        Long categoryId
) {
}
