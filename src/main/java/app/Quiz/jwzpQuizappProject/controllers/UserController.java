package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.config.Constants;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.users.UserDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(Constants.LOGGER_NAME);
    private final TokenService tokenService;
    private final UserService userService;

    public UserController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping
    public List<UserModel> getMultipleUsers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(value = "username", required = false) Optional<String> username
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets multiple users with params: {username: " + username.orElse("\"\"") + "}.");
        return userService.getMultipleUsers(username);
    }

    @GetMapping("/{id}")
    public UserModel getSingleUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) throws UserNotFoundException {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets user with id: " + id + ".");
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public UserModel getMe(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets himself.");
        return tokenService.getUserFromToken(token);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) throws UserNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to deactivate user with id: " + id + ".");
        userService.deactivateUser(id, token);
        log.info("User with email: " + userEmail + " deactivated user with id: " + id + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody UserDto user
    ) throws UserNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update user with id: " + user.userId() + ".");
        userService.updateUser(user, token);
        log.info("User with email: " + userEmail + " updated user with id: " + user.userId() + ".");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

