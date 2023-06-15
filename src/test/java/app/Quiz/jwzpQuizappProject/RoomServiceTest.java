package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.InvalidRoomDataException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomPutDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserRole;
import app.Quiz.jwzpQuizappProject.repositories.QuizRepository;
import app.Quiz.jwzpQuizappProject.repositories.RoomRepository;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.RoomService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {
    String token = "Bearer token";
    private RoomService roomService;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private ResultsService resultsService;
    @Mock
    private RoomAuthoritiesValidator roomAuthoritiesValidator;
    @Mock
    private Clock clock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        roomService = new RoomService(
                quizRepository,
                roomRepository,
                userRepository,
                tokenService,
                resultsService,
                roomAuthoritiesValidator,
                clock
        );
    }

    @Test
    public void getSingleRoom_WhenRoomExistsAndUserHasAuthority_ShouldReturnRoomModel() throws RoomNotFoundException, PermissionDeniedException {
        long roomId = 1L;
        Instant roomStartTime = Instant.parse("2015-04-29T10:15:30.00Z");
        UserModel user =  makeTokenServiceReturnUser();
        RoomModel room = new RoomModel();
        room.setStartTime(roomStartTime);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room)).thenReturn(true);

        Instant currentTime = Instant.parse("2018-04-29T10:15:30.00Z");
        when(clock.instant()).thenReturn(currentTime);

        RoomModel result = roomService.getSingleRoom(roomId, token);

        assertNotNull(result);
        assertEquals(room, result);
    }

    @Test
    public void getSingleRoom_WhenRoomExistsAndUserHasAuthorityButBeforeStartTime_ShouldReturnEraseQuizzesInRoomModel() throws RoomNotFoundException, PermissionDeniedException {
        long roomId = 1L;
        Instant roomStartTime = Instant.parse("2018-04-29T10:15:31.00Z");
        UserModel user = new UserModel();
        RoomModel room = new RoomModel();
        room.setStartTime(roomStartTime);

        var quizzesInRoom = new HashSet<QuizModel>();
        quizzesInRoom.add(new QuizModel());
        quizzesInRoom.add(new QuizModel());
        quizzesInRoom.add(new QuizModel());

        room.setQuizzes(quizzesInRoom);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room)).thenReturn(true);

        Instant currentTime = Instant.parse("2018-04-29T10:15:30.00Z");
        when(clock.instant()).thenReturn(currentTime);

        RoomModel result = roomService.getSingleRoom(roomId, token);

        assertNotNull(result);
        assertEquals(0, result.getQuizzes().size());
        verify(roomRepository, never()).save(any(RoomModel.class));
    }

    @Test
    public void getSingleRoom_WhenRoomDoesNotExist_ShouldThrowRoomNotFoundException() {
        long roomId = 1L;
        UserModel user =  makeTokenServiceReturnUser();
        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getSingleRoom(roomId, token));
    }

    @Test
    public void testGetUserRooms_Success() {
        UserModel user =  makeTokenServiceReturnUser();

        List<RoomModel> rooms = new ArrayList<>();
        var userRoom1 = new RoomModel();
        userRoom1.setOwner(user);
        rooms.add(userRoom1);
        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findAllByOwnerOrParticipantsContaining(user, user)).thenReturn(rooms);

        List<RoomModel> result = roomService.getUserRooms(token);

        assertEquals(rooms, result);
    }

    @Test
    public void testGetAllRooms_Success() throws PermissionDeniedException {
        UserModel user =  makeTokenServiceReturnAdmin();

        var rooms = new ArrayList<RoomModel>();
        rooms.add(new RoomModel());
        rooms.add(new RoomModel());

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findAll()).thenReturn(rooms);

        List<RoomModel> result = roomService.getAllRooms(token);

        assertEquals(rooms, result);
    }

    @Test
    public void testGetAllRooms_PermissionDeniedException() {
        UserModel user = makeTokenServiceReturnUser();
        user.setRoles(List.of(UserRole.USER));
        when(tokenService.getUserFromToken(token)).thenReturn(user);

        assertThrows(PermissionDeniedException.class, () -> roomService.getAllRooms(token));
    }


    @Test
    public void testCreateRoom_Success() throws InvalidRoomDataException {
        String token = "token";
        UserModel user =  makeTokenServiceReturnUser();
        RoomDto roomDto = new RoomDto("Room 1", Instant.now(), Instant.now().plusSeconds(3600));
        RoomModel room = new RoomModel(roomDto.name(), user, roomDto.startTime(), roomDto.endTime());

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.save(any(RoomModel.class))).thenReturn(room);

        RoomModel result = roomService.createRoom(roomDto, token);

        assertNotNull(result);
        assertEquals(roomDto.name(), result.getRoomName());
        assertEquals(user, result.getOwner());
        assertEquals(roomDto.startTime(), result.getStartTime());
        assertEquals(roomDto.endTime(), result.getEndTime());
        verify(roomRepository).save(any(RoomModel.class));
    }

    @Test
    public void testDeleteRoom_Success() throws RoomNotFoundException, PermissionDeniedException {
        String token = "token";
        long roomId = 1L;
        UserModel user =  makeTokenServiceReturnUser();
        RoomModel room = new RoomModel("Room 1", user, Instant.now(), Instant.now().plusSeconds(3600));

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(resultsService.getResultsForRoom(roomId, token)).thenReturn(Collections.emptyList());
        when(roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())).thenReturn(true);

        roomService.deleteRoom(roomId, token);

        verify(roomRepository).delete(room);
    }

    @Test
    public void testDeleteRoom_RoomNotFoundException() {
        String token = "token";
        long roomId = 1L;
        UserModel user =  makeTokenServiceReturnUser();

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(roomId, token));
    }

    @Test
    public void testDeleteRoom_PermissionDeniedException() {
        String token = "token";
        long roomId = 1L;
        UserModel user =  makeTokenServiceReturnUser();
        RoomModel room = new RoomModel("Room 1", user, Instant.now(), Instant.now().plusSeconds(3600));

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomAuthoritiesValidator.validateUserRoomEditAuthorities(user, room.getOwnerId())).thenReturn(false);

        assertThrows(PermissionDeniedException.class, () -> roomService.deleteRoom(roomId, token));
    }

    @Test
    public void testUpdateRoom_Success() throws RoomNotFoundException, UserNotFoundException {
        long roomId = 1L;
        Instant newRoomStartTime = Instant.parse("2015-04-29T10:15:30.00Z");
        Instant oldRoomStartTime = Instant.parse("2015-04-29T10:15:30.00Z");
        var owner = makeTokenServiceReturnUser();
        RoomPutDto roomDto = new RoomPutDto(roomId, "Updated Room",owner, newRoomStartTime,newRoomStartTime.plusSeconds(3600));
        RoomModel originalRoom = new RoomModel("Room 1", new UserModel(), oldRoomStartTime, oldRoomStartTime.plusSeconds(3600));

        when(roomRepository.findById(roomDto.id())).thenReturn(Optional.of(originalRoom));
        when(userRepository.findById(roomDto.owner().getId())).thenReturn(Optional.of(owner));
        when(roomRepository.save(originalRoom)).thenReturn(originalRoom);

        RoomModel result = roomService.updateRoom(roomDto);

        assertNotNull(result);
        assertEquals(roomDto.roomName(), result.getRoomName());
        assertEquals(roomDto.startTime(), result.getStartTime());
        assertEquals(roomDto.endTime(), result.getEndTime());
        assertEquals(roomDto.owner().getId(), result.getOwner().getId());
        verify(roomRepository).save(originalRoom);
    }

    @Test
    public void testUpdateRoom_RoomNotFoundException() {
        long roomId = 1L;
        Instant newRoomStartTime = Instant.parse("2015-04-29T10:15:30.00Z");
        var owner = makeTokenServiceReturnUser();
        RoomPutDto roomDto = new RoomPutDto(roomId, "Updated Room",owner, newRoomStartTime,newRoomStartTime.plusSeconds(3600));

        when(roomRepository.findById(roomDto.id())).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(roomDto));

        verify(roomRepository).findById(roomDto.id());
        verifyNoMoreInteractions(roomRepository, userRepository);
    }

    @Test
    public void testUpdateRoom_UserNotFoundException() {
        long roomId = 1L;
        Instant newRoomStartTime = Instant.parse("2015-04-29T10:15:30.00Z");
        Instant oldRoomStartTime = Instant.parse("2015-04-29T10:15:30.00Z");
        var owner = makeTokenServiceReturnUser();
        RoomPutDto roomDto = new RoomPutDto(roomId, "Updated Room",owner, newRoomStartTime,newRoomStartTime.plusSeconds(3600));
        RoomModel originalRoom = new RoomModel("Room 1", new UserModel(), oldRoomStartTime, oldRoomStartTime.plusSeconds(3600));

        when(roomRepository.findById(roomDto.id())).thenReturn(Optional.of(originalRoom));
        when(userRepository.findById(roomDto.owner().getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> roomService.updateRoom(roomDto));

        verify(roomRepository).findById(roomDto.id());
        verify(userRepository).findById(roomDto.owner().getId());
        verifyNoMoreInteractions(roomRepository, userRepository);
    }


    private UserModel makeTokenServiceReturnUser(){
        long userId = 1;
        var user = new UserModel();
        user.setRoles(List.of(UserRole.USER));
        user.setId(userId);

        when(tokenService.getUserFromToken(token)).thenReturn(user);

        return user;
    }

    private UserModel makeTokenServiceReturnAdmin(){
        long userId = 1;
        var user = new UserModel();
        user.setRoles(List.of(UserRole.ADMIN));
        user.setId(userId);

        when(tokenService.getUserFromToken(token)).thenReturn(user);

        return user;
    }
}
