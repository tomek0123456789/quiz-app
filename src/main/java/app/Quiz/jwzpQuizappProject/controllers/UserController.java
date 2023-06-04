package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.users.UserDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    final UserService userService;

    public UserController(UserRepository userRepository, TokenService tokenService, UserService userService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserModel>> getMultipleUsers(@RequestParam(value = "name", required = false) Optional<String> userName ){
        return userName.map(s -> ResponseEntity.ok(userService.getMultipleUsersByName(s))).orElseGet(() -> ResponseEntity.ok(userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingleUser(@PathVariable long id) throws UserNotFoundException {
        return ResponseEntity.ok( userService.getUserById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserModel> getMe(@RequestHeader("Authorization") String bearerToken) {
        return  ResponseEntity.ok(tokenService.getUserFromToken(bearerToken));
    }

    // TODO: TOMEK! Chyba tego juz nie uzywamy cnie?????
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody UserModel newUser) {
        this.userRepository.save(newUser);
        return ResponseEntity.ok(newUser.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token,
                                     @PathVariable long id) throws UserNotFoundException, PermissionDeniedException {
        userService.deactivateUser(token, id);

        return ResponseEntity.ok("ok");
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                     @RequestBody UserDto user) throws UserNotFoundException, PermissionDeniedException {
        userService.updateUser(token,user);
        throw new NotYetImplementedException();
    }
}

