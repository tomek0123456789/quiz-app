package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.RoomAuthoritiesValidator;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RoomService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TimeService timeService;
    private final TokenService tokenService;

    private final ResultsService resultsService;
    private final RoomAuthoritiesValidator roomAuthoritiesValidator;


    public RoomService(AnswerRepository answerRepository, QuestionRepository questionRepository, QuizRepository quizRepository, RoomRepository roomRepository, UserRepository userRepository, TimeService timeService, TokenService tokenService, ResultsService resultsService, RoomAuthoritiesValidator roomAuthoritiesValidator) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.timeService = timeService;
        this.tokenService = tokenService;
        this.resultsService = resultsService;
        this.roomAuthoritiesValidator = roomAuthoritiesValidator;
    }


    private RoomNotFoundException getPreparedRoomNotFoundException(long roomId) {
        return new RoomNotFoundException("Room with id: " + roomId + " was not found.");
    }


    private void throwPermissionDeniedException(long roomId) throws PermissionDeniedException {
        throw new PermissionDeniedException("You are neither an admin, nor an owner of a quiz with id: " + roomId + ".");
    }

    /////////////
    public RoomModel getSingleRoom(long roomId, String token) throws RoomNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room)) {
            throwPermissionDeniedException(roomId);
        }

        if(timeService.getCurrentTime().compareTo(room.getStartTime()) < 0){
            room.setQuizzes(Collections.emptySet());    // if room havent started, eresing (temporary) quizzes
        }

        return room;
    }

    public List<RoomModel> getUserRooms (String token) {
        var user = tokenService.getUserFromToken(token);
        return roomRepository.findAllByOwner(user);
    }

    public List<RoomModel> getAllRooms(String token) throws PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);

        if(!user.isAdmin()){
            throw new PermissionDeniedException(user.getName() + " is not an admin");
        }

        return roomRepository.findAll();
    }

    public RoomModel createRoom(RoomDto roomDto, String token) {
        var user = tokenService.getUserFromToken(token);
        var room = new RoomModel(roomDto.name(), user, roomDto.startTime(), roomDto.endTime());
        roomRepository.save(room);
        return room;
    }

    public RoomModel updateRoom(String token, long roomId, RoomDto updatedRoom){
        throw new NotYetImplementedException();
    }

    public void deleteRoom(long roomId, String token) throws RoomNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));

        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(roomId);
        }

        //todo delete everything in bulk in each delete function
        resultsService.deleteAllResults(resultsService.getResultsForRoom(token, roomId));


        room.getQuizzes().forEach(quizModel -> {
                            quizModel.removeRoom(room);
                            quizRepository.save(quizModel); //updateing quiz repo
                        });
        room.getParticipants().forEach(u -> {
            u.removeRoomParticipation(room);
            userRepository.save(u);
        });

        roomRepository.delete(room);
    }


    //////////////////////////////////////


    public void addUserToRoom(long roomId, long userId, String token) throws RoomNotFoundException, PermissionDeniedException, UserNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));
        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(roomId);
        }

        var userToAdd = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User you wanted to add with id:" + userId + " was not found."));

        room.addParticipant(userToAdd);
        userToAdd.addRoomParticipation(room);
        roomRepository.save(room);
        userRepository.save(userToAdd);
    }

    public void removeUserFromRoom(long roomId, long userToRemoveId, String token) throws RoomNotFoundException, UserNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var userToRemove = userRepository.findById(userToRemoveId).orElseThrow(() -> new UserNotFoundException("User you wanted to add with id:" + userToRemoveId + " was not found."));
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));

        if (!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())) {
            throwPermissionDeniedException(roomId);
        }

        if(!roomAuthoritiesValidator.validateUserRoomRemoveUserAuthorities(user, userToRemove)){
            throwPermissionDeniedException(roomId);
        }

        room.removeParticipant(userToRemove);
        userToRemove.removeRoomParticipation(room);

        roomRepository.save(room);
        userRepository.save(userToRemove);
    }

    public void addQuizToRoom(long roomId, long quizId, String token) throws RoomNotFoundException, QuizNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("quiz with id=" + quizId + "not found"));
        var room = roomRepository.findById(roomId).orElseThrow(() -> getPreparedRoomNotFoundException(roomId));

        if(!roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())){
            throw new PermissionDeniedException(user.getName() + " does not have authorities to edit room with id=" + room.getId());
        }

        room.addQuiz(quiz);
        quiz.addRoom(room);
        this.roomRepository.save(room);
        this.quizRepository.save(quiz);
    }
}
