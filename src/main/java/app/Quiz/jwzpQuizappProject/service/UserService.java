package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.exceptions.UserAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.models.auth.RegisterRequest;
import app.Quiz.jwzpQuizappProject.models.users.SecurityUser;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " was not found."));
    }

    public void saveUser(RegisterRequest registerRequest) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new UserAlreadyExistsException("Account with email " + registerRequest.email() + " already exists.");
        }
        var user = new UserModel(registerRequest.name(), registerRequest.email(), passwordEncoder.encode(registerRequest.password()));
        userRepository.save(user);
    }
    public UserModel getUser(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " was not found.");
        }
        return user.get();
    }
//TODO

//    public void updateUser() {
//        userRepository.
//    }
    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }

}
