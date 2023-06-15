package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.RoomAuthoritiesValidator;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizStatus;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPatchDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPutDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.QuizRepository;
import app.Quiz.jwzpQuizappProject.repositories.RoomRepository;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RoomService {
    private final QuizRepository quizRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ResultsService resultsService;
    private final RoomAuthoritiesValidator roomAuthoritiesValidator;
    private final Clock clock;


    public RoomService(QuizRepository quizRepository, RoomRepository roomRepository, UserRepository userRepository, TokenService tokenService, ResultsService resultsService, RoomAuthoritiesValidator roomAuthoritiesValidator, Clock clock) {
        this.quizRepository = quizRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.resultsService = resultsService;
        this.roomAuthoritiesValidator = roomAuthoritiesValidator;
        this.clock = clock;
    }

    private RoomNotFoundException getPreparedRoomNotFoundException(long roomId) {
        return new RoomNotFoundException("Room with id: " + roomId + " was not found.");
    }

    private void throwPermissionDeniedException(String username, long roomId) throws PermissionDeniedException {
        throw new PermissionDeniedException(username + " does not have authorities to edit a room with id: " + roomId + ".");
    }

    /////////////
    public RoomModel getSingleRoom(long roomId, String token) throws RoomNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room)) {
            throwPermissionDeniedException(user.getName(), roomId);
        }

        if (clock.instant().isBefore(room.getStartTime())) {
            room.setQuizzes(Collections.emptySet());    // if room hasn't started, erase (temporarily) quizzes
        }
        return room;
    }

    public List<RoomModel> getUserRooms(String token) {
        var user = tokenService.getUserFromToken(token);
        return roomRepository.findAllByOwnerOrParticipantsContaining(user, user);
    }

    public List<RoomModel> getAllRooms(String token) throws PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        if (!user.isAdmin()) {
            throw new PermissionDeniedException(user.getName() + " is not an admin.");
        }
        return roomRepository.findAll();
    }

    public RoomModel createRoom(RoomDto roomDto, String token) {
        var user = tokenService.getUserFromToken(token);
        var room = new RoomModel(roomDto.name(), user, roomDto.startTime(), roomDto.endTime());
        roomRepository.save(room);
        return room;
    }

    public void deleteRoom(long roomId, String token) throws RoomNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(user.getName(), roomId);
        }

        resultsService.deleteAllResults(resultsService.getResultsForRoom(roomId, token));

        //todo refactor this
        List<QuizModel> quizzes = new ArrayList<>();
        room.getQuizzes().forEach(quizModel -> {
            quizModel.removeRoom(room);
            quizzes.add(quizModel);
        });
        quizRepository.saveAll(quizzes);

        List<UserModel> users = new ArrayList<>();
        room.getParticipants().forEach(u -> {
            u.removeRoomParticipation(room);
            users.add(u);
        });
        userRepository.saveAll(users);

        roomRepository.delete(room);
    }


    public RoomModel updateRoom(RoomPutDto roomDto) throws RoomNotFoundException, UserNotFoundException {
        var originalRoom = roomRepository.findById(roomDto.id()).orElseThrow(() -> getPreparedRoomNotFoundException(roomDto.id()));
        //validate if new owner exists
        userRepository.findById(roomDto.owner().getId()).orElseThrow(() -> new UserNotFoundException("User with id: " + roomDto.owner().getId() + " was not found."));
        originalRoom.updateWithPutDto(roomDto);
        roomRepository.save(originalRoom);
        return originalRoom;
    }


    public RoomModel updateRoom(RoomPatchDto roomDto, String token) throws PermissionDeniedException, RoomNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var originalRoom = roomRepository.findById(roomDto.id()).orElseThrow(() -> getPreparedRoomNotFoundException(roomDto.id()));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, originalRoom.getOwnerId())) {
            throwPermissionDeniedException(user.getName(), roomDto.id());
        }

        originalRoom.updateWithPatchDto(roomDto);
        roomRepository.save(originalRoom);
        return originalRoom;
    }


    public void addUserToRoom(long roomId, long userId, String token) throws RoomNotFoundException, PermissionDeniedException, UserNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(user.getName(), roomId);
        }

        var userToAdd = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User you wanted to add with id: " + userId + " was not found."));
        room.addParticipant(userToAdd);
        userToAdd.addRoomParticipation(room);
        roomRepository.save(room);
        userRepository.save(userToAdd);
    }

    public void removeUserFromRoom(long roomId, long userToRemoveId, String token) throws RoomNotFoundException, UserNotFoundException, PermissionDeniedException {
        var userSendingRequest = tokenService.getUserFromToken(token);
        var userToRemove = userRepository.findById(userToRemoveId).orElseThrow(() -> new UserNotFoundException("User you wanted to add with id:" + userToRemoveId + " was not found."));
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(userSendingRequest, room.getOwnerId())
             || !roomAuthoritiesValidator.validateUserRoomRemoveUserAuthorities(userSendingRequest, userToRemove)
        ) {
            throwPermissionDeniedException(userSendingRequest.getName(), roomId);
        }

        room.removeParticipant(userToRemove);
        userToRemove.removeRoomParticipation(room);

        roomRepository.save(room);
        userRepository.save(userToRemove);
    }

    public void addQuizToRoom(long roomId, long quizId, String token) throws RoomNotFoundException, QuizNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(user.getName(), roomId);
        }
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz with id: " + quizId + " was not found."));
        if (quiz.getQuizStatus() == QuizStatus.INVALID || quiz.getQuizStatus() == QuizStatus.VALIDATABLE) {
            throw new QuizNotFoundException("Quiz with id: " + quizId + " was not found.");
        }
        room.addQuiz(quiz);
        quiz.addRoom(room);
        roomRepository.save(room);
        quizRepository.save(quiz);
    }

    public void removeQuizFromRoom(long roomId, long quizId, String token) throws QuizNotFoundException, RoomNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz with id: " + quizId + " was not found."));
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(user.getName(), roomId);
        }

        room.removeQuiz(quiz);
        quiz.removeRoom(room);
        roomRepository.save(room);
        quizRepository.save(quiz);
    }


}
