package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.auth.RegisterDto;
import app.Quiz.jwzpQuizappProject.models.users.SecurityUser;
import app.Quiz.jwzpQuizappProject.models.users.UserDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserStatus;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final Clock clock;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.tokenService = tokenService;
    }

    private boolean validateUserDeleteUserAuthorities(UserModel executor, UserModel receiver) {
        return executor.isAdmin() || executor == receiver;
    }

    private boolean validateUserEditUserAuthorities(UserModel executor, UserModel receiver) {
        return executor.isAdmin() || executor == receiver;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " was not found."));
    }

//    why no salt?
//    https://stackoverflow.com/a/71813446
//    https://docs.spring.io/spring-security/site/docs/3.2.0.RC1/reference/html/crypto.html (last section, 25.4)
    public void saveUser(RegisterDto registerDto) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(registerDto.email())) {
            throw new UserAlreadyExistsException("An account with email " + registerDto.email() + " already exists.");
        }
        if (userRepository.existsByName(registerDto.name())) {
            throw new UserAlreadyExistsException("An account with name " + registerDto.email() + " already exists.");
        }
        var user = new UserModel(registerDto.name(), registerDto.email(), passwordEncoder.encode(registerDto.password()), clock.instant());
        userRepository.save(user);
    }

    public UserModel getUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id: " + id + " was not found."));
    }

    public UserModel getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email: " + email + " was not found."));
    }

    public void deleteUser(String token, String email) throws PermissionDeniedException, UserNotFoundException {
        var executor = tokenService.getUserFromToken(token);
        var receiver = getUserByEmail(email);

        if (!validateUserDeleteUserAuthorities(executor, receiver)) {
            throw new PermissionDeniedException("User with name: " + executor.getName() + " does not have authority to delete user with name: " + receiver.getName() + ".");
        }

        userRepository.deleteByEmail(email);
    }

    public void deactivateUser(long id, String token) throws PermissionDeniedException, UserNotFoundException {
        var executor = tokenService.getUserFromToken(token);
        var receiver = getUserById(id);

        if (!validateUserDeleteUserAuthorities(executor, receiver)) {
            throw new PermissionDeniedException("User with name: " + executor.getName() + " does not have authority to delete user with name: " + receiver.getName() + ".");
        }

        receiver.setStatus(UserStatus.DEACTIVATED);

        userRepository.save(receiver);
    }

    public List<UserModel> getMultipleUsers(Optional<String> username) {
        if (username.isPresent()) {
            return userRepository.findAllByNameContaining(username.get());
        }
        return userRepository.findAll();
    }

    public void updateUser(UserDto userDto, String token) throws UserNotFoundException, PermissionDeniedException {
        var executor = tokenService.getUserFromToken(token);
        var receiver = getUserById(userDto.userId());
        if (!validateUserDeleteUserAuthorities(executor, receiver)) {
            throw new PermissionDeniedException("User with name: " + executor.getName() + " does not have authority to delete user with name: " + receiver.getName() + ".");
        }

        if (userDto.email() != null) {
            receiver.setEmail(userDto.email());
        }
        if (userDto.password() != null) {
            receiver.setPassword(passwordEncoder.encode(userDto.password()));
        }
        userRepository.save(receiver);
    }
}
