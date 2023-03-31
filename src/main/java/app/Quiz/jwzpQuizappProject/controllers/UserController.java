package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.controllers.UserController;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
public class UserController {
    private  final UserRepository userRepository;
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(this.userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity getSingleuser(@PathVariable long id) {
        return ResponseEntity.ok(this.userRepository.findById(id));
    }

    @PostMapping()
    public ResponseEntity createUser(@RequestBody UserModel newUser) {
        this.userRepository.save(newUser);
        return ResponseEntity.ok(newUser.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable long id) {
        this.userRepository.deleteAllById(Collections.singleton(id));
        return ResponseEntity.ok("ok");
    }

//    @PutMapping("/{id}")
//    public ResponseEntity updateUser(@RequestBody UserModel updatedUser) {
//        this.userRepository.upda
//        return ResponseEntity.ok("ok");
//    }
}

