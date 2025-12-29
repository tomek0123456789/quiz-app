package app.Quiz.jwzpQuizappProject.models.results;

public record UserRoomResultDto(
        long userId,
        String userName,
        long totalScore
) {
}
