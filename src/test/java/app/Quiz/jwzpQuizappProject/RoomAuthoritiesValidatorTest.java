package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoomAuthoritiesValidatorTest {
    private RoomAuthoritiesValidator validator;
    private UserModel adminUser;
    private UserModel regularUser;
    private UserModel roomOwner;
    private UserModel otherUser;
    private RoomModel room;

    @BeforeEach
    public void setup() {
        validator = new RoomAuthoritiesValidator();
        adminUser = new UserModel();
        adminUser.setId(1L);
        adminUser.setName("admin");
        adminUser.setRoles(List.of(UserRole.ADMIN));

        regularUser = new UserModel();
        regularUser.setId(2L);
        regularUser.setRoles(List.of(UserRole.USER));

        roomOwner = new UserModel();
        roomOwner.setId(3L);
        roomOwner.setRoles(List.of(UserRole.USER));

        otherUser = new UserModel();
        otherUser.setId(4L);
        otherUser.setRoles(List.of(UserRole.USER));

        room = new RoomModel();
        room.setId(1);
        room.setRoomName("Room 1");
        room.setOwner(roomOwner);

        room.addParticipant(regularUser);
    }

    @Test
    public void checkRoomOwnership_WhenSameOwnerId_ReturnsTrue() {
        assertTrue(validator.checkRoomOwnership(3, 3));
    }

    @Test
    public void checkRoomOwnership_WhenDifferentOwnerId_ReturnsFalse() {
        assertFalse(validator.checkRoomOwnership(2, 3));
    }

    @Test
    public void validateUserRoomEditAuthorities_WhenAdmin_ReturnsTrue() {
        assertTrue(validator.validateUserRoomEditAuthorities(adminUser, 3));
    }

    @Test
    public void validateUserRoomEditAuthorities_WhenOwner_ReturnsTrue() {
        assertTrue(validator.validateUserRoomEditAuthorities(roomOwner, 3));
    }

    @Test
    public void validateUserRoomEditAuthorities_WhenRegularUser_ReturnsFalse() {
        assertFalse(validator.validateUserRoomEditAuthorities(regularUser, 3));
    }

    @Test
    public void validateUserRoomRemoveUserAuthorities_WhenAdmin_ReturnsTrue() {
        assertTrue(validator.validateUserRoomRemoveUserAuthorities(adminUser, regularUser));
    }

    @Test
    public void validateUserRoomRemoveUserAuthorities_WhenSameUser_ReturnsTrue() {
        assertTrue(validator.validateUserRoomRemoveUserAuthorities(regularUser, regularUser));
    }

    @Test
    public void validateUserRoomRemoveUserAuthorities_WhenDifferentUser_ReturnsFalse() {
        assertFalse(validator.validateUserRoomRemoveUserAuthorities(regularUser, otherUser));
    }

    @Test
    public void validateUserRoomInfoAuthorities_WhenAdmin_ReturnsTrue() {
        assertTrue(validator.validateUserRoomInfoAuthorities(adminUser, room));
    }

    @Test
    public void validateUserRoomInfoAuthorities_WhenOwner_ReturnsTrue() {
        assertTrue(validator.validateUserRoomInfoAuthorities(roomOwner, room));
    }

    @Test
    public void validateUserRoomInfoAuthorities_WhenParticipant_ReturnsTrue() {
        assertTrue(validator.validateUserRoomInfoAuthorities(regularUser, room));
    }

    @Test
    public void validateUserRoomInfoAuthorities_WhenNonParticipant_ReturnsFalse() {
        assertFalse(validator.validateUserRoomInfoAuthorities(otherUser, room));
    }
}
