package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.users.UserDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final TokenService tokenService;
    private final UserService userService;

    public UserController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping
    public List<UserModel> getMultipleUsers(
            @RequestParam(value = "username", required = false) Optional<String> username
    ) {
        return userService.getMultipleUsers(username);
    }

    @GetMapping("/{id}")
    public UserModel getSingleUser(
            @PathVariable long id
    ) throws UserNotFoundException {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public UserModel getMe(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        return tokenService.getUserFromToken(token);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) throws UserNotFoundException, PermissionDeniedException {
        userService.deactivateUser(id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody UserDto user
    ) throws UserNotFoundException, PermissionDeniedException {
        userService.updateUser(user, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

