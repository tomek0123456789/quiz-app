package app.Quiz.jwzpQuizappProject.Controllers;

import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("users/")
public class UserController {
    private  final UserRepository userRepository;
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public ResponseEntity users() {
        return ResponseEntity.ok(this.userRepository.findAll());
    }
}

