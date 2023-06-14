package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.auth.RegisterDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserRole;
import app.Quiz.jwzpQuizappProject.models.users.UserStatus;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    String token = "token";
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    private UserService userService;
    private Clock clock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        clock = Clock.systemUTC();
        userService = new UserService(userRepository, passwordEncoder, tokenService, clock);
    }

    @Test
    public void saveUser_WhenUserDoesNotExist_ShouldSaveUser() throws UserAlreadyExistsException {
        RegisterDto registerDto = new RegisterDto("testuser", "test@example.com", "password");
        when(userRepository.existsByEmail(registerDto.email())).thenReturn(false);
        when(userRepository.existsByName(registerDto.name())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.password())).thenReturn("encodedPassword");

        userService.saveUser(registerDto);

        verify(userRepository, times(1)).existsByEmail(registerDto.email());
        verify(userRepository, times(1)).existsByName(registerDto.name());
        verify(passwordEncoder, times(1)).encode(registerDto.password());
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    public void saveUser_WhenUserWithEmailAlreadyExists_ShouldThrowUserAlreadyExistsException() {
        RegisterDto registerDto = new RegisterDto("testuser", "test@example.com", "password");
        when(userRepository.existsByEmail(registerDto.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(registerDto));
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    public void saveUser_WhenUserWithNameAlreadyExists_ShouldThrowUserAlreadyExistsException() {
        RegisterDto registerDto = new RegisterDto("testuser", "test@example.com", "password");
        when(userRepository.existsByEmail(registerDto.email())).thenReturn(false);
        when(userRepository.existsByName(registerDto.name())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(registerDto));
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    public void getUserById_WhenUserExists_ShouldReturnUser() throws UserNotFoundException {
        long userId = 1;
        UserModel user = new UserModel(userId, "testuser", "test@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserModel result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStatus(), result.getStatus());
        assertEquals(user.getRoles(), result.getRoles());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getSalt(), result.getSalt());
    }

    @Test
    public void getUserById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void getUserByEmail_WhenUserExists_ShouldReturnUser() throws UserNotFoundException {
        String email = "test@example.com";
        UserModel user = new UserModel(1L, "testuser", email, UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserModel result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStatus(), result.getStatus());
        assertEquals(user.getRoles(), result.getRoles());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getSalt(), result.getSalt());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void getUserByEmail_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    public void deleteUser_WhenExecutorHasAuthority_ShouldDeleteUser() throws PermissionDeniedException, UserNotFoundException {
        String email = "receiver@example.com";
        UserModel AdminExecutor = new UserModel(1L, "executor", "executor@example.com", UserStatus.ACTIVE, List.of(UserRole.ADMIN), "password", "salt");
        UserModel receiver = new UserModel(2L, "receiver", email, UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(AdminExecutor);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiver));
        when(userRepository.existsByEmail(email)).thenReturn(true);

        userService.deleteUser(token, email);

        verify(userRepository, times(1)).deleteByEmail(email);
    }

    @Test
    public void deleteUser_WhenExecutorIsReceiver_ShouldDeleteUser() throws PermissionDeniedException, UserNotFoundException {
        String email = "receiver@example.com";
        UserModel receiverAndSelfExecutor = new UserModel(2L, "receiver", email, UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(receiverAndSelfExecutor);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiverAndSelfExecutor));
        when(userRepository.existsByEmail(email)).thenReturn(true);

        userService.deleteUser(token, email);

        verify(userRepository, times(1)).deleteByEmail(email);
    }

    @Test
    public void deleteUser_WhenExecutorDoesNotHaveAuthority_ShouldThrowPermissionDeniedException() {
        String email = "test@example.com";
        UserModel executor = new UserModel(1L, "executor", "executor@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        UserModel receiver = new UserModel(2L, "receiver", email, UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(executor);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(receiver));
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(PermissionDeniedException.class, () -> userService.deleteUser(token, email));

        verify(userRepository, never()).existsByEmail(email);
        verify(userRepository, never()).deleteByEmail(email);
    }

    @Test
    public void deleteUser_WhenUserDoesNotExist_ShouldThrowPermissionDeniedException() {
        String email = "test@example.com";
        UserModel executor = new UserModel(1L, "executor", "executor@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(executor);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(token, email));
    }

    @Test
    public void deactivateUser_WhenExecutorHasAuthority_ShouldDeactivateUser() throws PermissionDeniedException, UserNotFoundException {
        long userId = 2L;
        var adminAuthorities = List.of(UserRole.ADMIN);
        UserModel executor = new UserModel(1L, "executor", "executor@example.com", UserStatus.ACTIVE, adminAuthorities, "password", "salt");
        UserModel receiver = new UserModel(userId, "receiver", "test@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(executor);
        when(userRepository.findById(userId)).thenReturn(Optional.of(receiver));

        userService.deactivateUser(userId, token);

        assertEquals(UserStatus.DEACTIVATED, receiver.getStatus());
        verify(userRepository, times(1)).save(receiver);
    }

    @Test
    public void deactivateUser_WhenExecutorDoesNotHaveAuthority_ShouldThrowPermissionDeniedException() {
        long userId = 2L;
        UserModel executor = new UserModel(1L, "executor", "executor@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        UserModel receiver = new UserModel(userId, "receiver", "test@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(executor);
        when(userRepository.findById(userId)).thenReturn(Optional.of(receiver));

        assertThrows(PermissionDeniedException.class, () -> userService.deactivateUser(userId, token));

        verify(tokenService, times(1)).getUserFromToken(token);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(receiver);
    }

    @Test
    public void deactivateUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        long userId = 2L;
        UserModel executor = new UserModel(1L, "executor", "executor@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        when(tokenService.getUserFromToken(token)).thenReturn(executor);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(userId, token));

        verify(tokenService, times(1)).getUserFromToken(token);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    public void getMultipleUsers_WhenUsernamePresent_ShouldReturnMatchingUsers() {
        String username = "test";
        UserModel user1 = new UserModel(1L, "testuser1", "test1@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        UserModel user2 = new UserModel(2L, "testuser2", "test2@example.com", UserStatus.ACTIVE, new ArrayList<>(), "password", "salt");
        List<UserModel> users = List.of(user1, user2);
        when(userRepository.findAllByNameContaining(username)).thenReturn(users);

        List<UserModel> result = userService.getMultipleUsers(Optional.of(username));

        assertNotNull(result);
        assertEquals(users.size(), result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));

        verify(userRepository, times(1)).findAllByNameContaining(username);
    }

    @Test
    public void getMultipleUsers_WhenUsernameNotPresent_ShouldReturnEmptyList() {
        String username = "test";
        when(userRepository.findAllByNameContaining(username)).thenReturn(new ArrayList<>());

        List<UserModel> result = userService.getMultipleUsers(Optional.of(username));

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAllByNameContaining(username);
    }
}