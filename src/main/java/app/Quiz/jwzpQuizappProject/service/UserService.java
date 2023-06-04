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

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    private boolean validateUserDeleteUserAuthorities(UserModel executor, UserModel receiver){
        return executor.isAdmin() || executor == receiver;
    }

    private boolean validateUserEditUserAuthorities(UserModel executor, UserModel receiver){
        return executor.isAdmin() || executor == receiver;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " was not found."));
    }

    public void saveUser(RegisterDto registerDto) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(registerDto.email())) {
            throw new UserAlreadyExistsException("Account with email " + registerDto.email() + " already exists.");
        }
        var user = new UserModel(registerDto.name(), registerDto.email(), passwordEncoder.encode(registerDto.password()));
        userRepository.save(user);
    }

    public UserModel getUser(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " was not found.");
        }
        return user.get();
    }

    public void deleteUser(String token, String email) throws PermissionDeniedException {
        var executor = tokenService.getUserFromToken(token);
        var receiver = getUser(email);

        if(!validateUserDeleteUserAuthorities(executor,receiver )){
            throw  new PermissionDeniedException(executor.getName() +  " does not have authorities to delete " + receiver.getName());
        }

        userRepository.deleteByEmail(email);
    }

    public void deactivateUser(String token, long id) throws PermissionDeniedException, UserNotFoundException {
        var executor = tokenService.getUserFromToken(token);
        var receiver = getUserById(id);

        if(!validateUserDeleteUserAuthorities(executor,receiver )){
            throw  new PermissionDeniedException(executor.getName() +  " does not have authorities to delete " + receiver.getName());
        }

        receiver.setStatus(UserStatus.DEACTIVATED);

        userRepository.save(receiver);
    }

    public List<UserModel> getMultipleUsersByName(String userName){
        return this.userRepository.findByName(userName);
    }

    public List<UserModel> getAllUsers(){
        return this.userRepository.findAll();
    }

    public UserModel getUserById(long id) throws UserNotFoundException {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id=" + id + " doesn't exist"));
    }

    public void updateUser(String token, UserDto user) throws UserNotFoundException, PermissionDeniedException {
        var executor = tokenService.getUserFromToken(token);
        var receiver = getUserById(user.userId());

        if(!validateUserDeleteUserAuthorities(executor,receiver )){
            throw  new PermissionDeniedException(executor.getName() +  " does not have authorities to update " + receiver.getName());
        }

        // TODO: not sure how to do this...

    }
}
