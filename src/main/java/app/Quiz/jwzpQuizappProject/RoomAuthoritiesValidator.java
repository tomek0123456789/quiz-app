package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration

public class RoomAuthoritiesValidator {
    @Bean
    public RoomAuthoritiesValidator getRoomAuthoritiesValidator() {
        return new RoomAuthoritiesValidator();
    }

    public boolean checkRoomOwnership(long userId, long roomOwnerId) {
        return userId == roomOwnerId;
    }

    public boolean validateUserRoomEditAuthorities(UserModel user, long roomOwnerId) {
        return user.isAdmin() || checkRoomOwnership(user.getId(), roomOwnerId);
    }// basically checks if executor can (for example) remove receiver from room

    public boolean validateUserRoomRemoveUserAuthorities(UserModel executor, UserModel receiver) {
        return executor.isAdmin() || executor == receiver;
    }

    public boolean validateUserRoomInfoAuthorities(UserModel user, RoomModel room) {
        return validateUserRoomEditAuthorities(user, room.getOwnerId()) || room.getParticipants().contains(user);
    }
}