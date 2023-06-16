package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.config.Constants;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.TimeExceededException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.InvalidRoomDataException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.results.ResultsDto;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPatchDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPutDto;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.RoomService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/myrooms")
public class RoomController {
    private final Logger log = LoggerFactory.getLogger(Constants.LOGGER_NAME);
    private final ResultsService resultsService;
    private final RoomService roomService;
    private final TokenService tokenService;

    public RoomController(ResultsService resultsService, RoomService roomService, TokenService tokenService) {
        this.resultsService = resultsService;
        this.roomService = roomService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{roomId}")
    public RoomModel getSingleRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId
    ) throws RoomNotFoundException, PermissionDeniedException {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets a room with id: " + roomId + ".");
        return roomService.getSingleRoom(roomId, token);
    }

    @PostMapping
    public ResponseEntity<RoomModel> createRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RoomDto roomDto
    ) throws InvalidRoomDataException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to create a room.");
        var createdRoom = roomService.createRoom(roomDto, token);
        log.info("User with email: " + userEmail + " created a room, room: " + createdRoom + ".");
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomModel> updateRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RoomPutDto roomPutDto
    ) throws RoomNotFoundException, UserNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update a room.");
        var updatedRoom = roomService.updateRoom(roomPutDto);
        log.info("User with email: " + userEmail + " updated a room, room: " + updatedRoom + ".");
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<RoomModel> updateRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RoomPatchDto roomPatchDto
    ) throws RoomNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update a room.");
        var updatedRoom = roomService.updateRoom(roomPatchDto, token);
        log.info("User with email: " + userEmail + " updated a room, room: " + updatedRoom + ".");
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId
    ) throws RoomNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to delete a room with id: " + roomId + ".");
        roomService.deleteRoom(roomId, token);
        log.info("User with email: " + userEmail + " deleted a room with id: " + roomId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    ////////////////////////////////////////////////////////////////////////////

    @PatchMapping("/{roomId}/users/{userId}")
    public ResponseEntity<String> addParticipantToRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId,
            @PathVariable long userId
    ) throws UserNotFoundException, RoomNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to add a user with id: " + userId + " to a room with id: " + roomId + ".");
        roomService.addUserToRoom(roomId, userId, token);
        log.info("User with email: " + userEmail + " added a user with id: " + userId + " to a room with id: " + roomId + ".");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}/users/{userId}")
    public ResponseEntity<HttpStatus> removeParticipantFromRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId,
            @PathVariable long userId
    ) throws UserNotFoundException, RoomNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to remove a user with id: " + userId + " from a room with id: " + roomId + ".");
        roomService.removeUserFromRoom(roomId, userId, token);
        log.info("User with email: " + userEmail + " removed a user with id: " + userId + " from a room with id: " + roomId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/results")
    public List<ResultsModel> getRoomResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) throws RoomNotFoundException, PermissionDeniedException {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets results from a room with id: " + id + ".");
        return resultsService.getResultsForRoom(id, token);
    }

    @PostMapping("/{id}/results")
    public ResponseEntity<ResultsModel> createResults(
            @PathVariable long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody ResultsDto results
    ) throws RoomNotFoundException, AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, TimeExceededException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to create results for a room with id: " + id + ".");
        var createdResults = resultsService.createResultsForRoom(results, id, token);
        log.info("User with email: " + userEmail + " created results for a room with id: " + id + ".");
        return new ResponseEntity<>(createdResults, HttpStatus.CREATED);
    }

    @GetMapping
    public List<RoomModel> getAllRooms(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets all rooms he's in.");
        return roomService.getUserRooms(token);
    }

    @PatchMapping("/{id}/quizzes/{quizId}")
    public ResponseEntity<String> addQuizToRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id,
            @PathVariable long quizId
    ) throws RoomNotFoundException, QuizNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to add quiz with id: " + quizId + " to a room with id: " + id + ".");
        roomService.addQuizToRoom(id, quizId, token);
        log.info("User with email: " + userEmail + " added a quiz with id: " + quizId + " to a room with id: " + id + ".");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/quizzes/{quizId}")
    public ResponseEntity<String> removeQuizFromRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id,
            @PathVariable long quizId
    ) throws RoomNotFoundException, QuizNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to remove a quiz with id: " + quizId + " from a room with id: " + id + ".");
        roomService.removeQuizFromRoom(id, quizId, token);
        log.info("User with email: " + userEmail + " removed a quiz with id: " + quizId + " from a room with id: " + id + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
