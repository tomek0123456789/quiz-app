package app.Quiz.jwzpQuizappProject.models.rooms;

import java.time.Instant;

public record RoomDto(
        String name,
        Instant startTime,
        Instant endTime
) {
}
