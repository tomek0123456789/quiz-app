package app.Quiz.jwzpQuizappProject.models.results;

public record QuestionAndUsersAnswerPatchDto(
        Long id,
        Long quizId,
        Integer questionOrdNum,
        Integer userAnswerOrdNum
) {
}
