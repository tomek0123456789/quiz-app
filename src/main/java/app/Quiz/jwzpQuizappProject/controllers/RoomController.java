package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.results.ResultsDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPatchDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPutDto;
import app.Quiz.jwzpQuizappProject.repositories.QuizRepository;
import app.Quiz.jwzpQuizappProject.repositories.ResultsRepository;
import app.Quiz.jwzpQuizappProject.repositories.RoomRepository;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.RoomService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/myrooms")
public class RoomController {

    private final ResultsService resultsService;
    private final RoomService roomService;

    public RoomController(ResultsService resultsService, RoomService roomService) {
        this.resultsService = resultsService;
        this.roomService = roomService;
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getSingleRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId
    ) throws RoomNotFoundException, PermissionDeniedException {
        return new ResponseEntity<>(roomService.getSingleRoom(roomId, token), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomModel> createRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RoomDto roomDto
    ) {
        return new ResponseEntity<>(roomService.createRoom(roomDto, token), HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomModel> updateRoom(    // No need for checking token since endpoint is admin only
            @RequestBody RoomPutDto roomPutDto
            ) throws RoomNotFoundException, PermissionDeniedException, UserNotFoundException {
        return new ResponseEntity<>(roomService.updateRoom(roomPutDto), HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<RoomModel> updateRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RoomPatchDto roomPatchDto
    ) throws RoomNotFoundException, PermissionDeniedException, UserNotFoundException {
        return new ResponseEntity<>(roomService.updateRoom(token,roomPatchDto), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId
    ) throws RoomNotFoundException, PermissionDeniedException {
        roomService.deleteRoom(roomId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    ////////////////////////////////////////////////////////////////////////////

    @PatchMapping("/{roomId}/users/{userId}")
    public ResponseEntity<?> addParticipantToRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId,
            @PathVariable long userId
    ) throws UserNotFoundException, RoomNotFoundException, PermissionDeniedException {
        roomService.addUserToRoom(roomId, userId, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}/users/{userId}")
    public ResponseEntity<?> removeParticipantFromRoom(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long roomId,
            @PathVariable long userId
    ) throws UserNotFoundException, RoomNotFoundException, PermissionDeniedException {
        roomService.removeUserFromRoom(roomId, userId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getRoomResults( @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                             @PathVariable long id) throws RoomNotFoundException, PermissionDeniedException {
        return ResponseEntity.ok(this.resultsService.getResultsForRoom(token, id));
    }

    @PostMapping("/{id}/results")
    public ResponseEntity<?> createResults(
            @PathVariable long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody ResultsDto results) throws RoomNotFoundException, AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists {
        var newResult = this.resultsService.createResultsForRoom(results,id, token);
        return ResponseEntity.ok(newResult);

    }

    @GetMapping()
    public ResponseEntity<?> getAllRooms( @RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws RoomNotFoundException, PermissionDeniedException {
        var myRooms = this.roomService.getMyRooms(token);
        return ResponseEntity.ok(myRooms);
    }

    @PostMapping("/{id}/quizzes/{quizId}")
    public ResponseEntity<?> addQuizToRoom(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable long id, @PathVariable long quizId) throws RoomNotFoundException, QuizNotFoundException, PermissionDeniedException {
        roomService.addQuizToRoom(token, id, quizId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
