package app.Quiz.jwzpQuizappProject.models.results;

public record ResultsPatchDto(
        long resultsId,
        Long roomId,
        Long score,
        Long ownerId
) {
}
