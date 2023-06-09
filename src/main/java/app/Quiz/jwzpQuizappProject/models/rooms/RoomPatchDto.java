package app.Quiz.jwzpQuizappProject.models.rooms;

import app.Quiz.jwzpQuizappProject.models.users.UserModel;

import java.time.Instant;

public record RoomPatchDto(
        long id,
        String roomName,
        Instant startTime,
        Instant endTime
)
{ }
