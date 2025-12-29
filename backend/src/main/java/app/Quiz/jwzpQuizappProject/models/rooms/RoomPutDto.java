package app.Quiz.jwzpQuizappProject.models.rooms;

import app.Quiz.jwzpQuizappProject.models.users.UserModel;

import java.time.Instant;

public record RoomPutDto(
        long id,
        String roomName,
        UserModel owner,
        Instant startTime,
        Instant endTime
) {
}
