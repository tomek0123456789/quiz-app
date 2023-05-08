package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserStatus;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(this.userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity getSingleUser(@PathVariable long id) {
        return ResponseEntity.ok(this.userRepository.findById(id));
    }

    //    private String getUserName() {
//        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        Jwt jwt = (Jwt) authenticationToken.getCredentials();
//        String email = (String) jwt.getClaims().get("exp");
//        return email;
//    }
    @GetMapping("/me")
    public ResponseEntity getMe(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.split(" ")[1];

        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        return ResponseEntity.ok(payload);
    }

    @PostMapping()
    public ResponseEntity createUser(@RequestBody UserModel newUser) {
        this.userRepository.save(newUser);
        return ResponseEntity.ok(newUser.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable long id) {
        Optional<UserModel> user = this.userRepository.findById(id);
        user.ifPresent(userModel -> userModel.setStatus(UserStatus.DEACTIVATED));

        user.ifPresent(this.userRepository::save);

        return ResponseEntity.ok("ok");
    }

    // TODO: check if authorized (user in request body must be same person as UserModel sent in rb, or admin ofc)
    @PutMapping()
    public ResponseEntity updateUser(@RequestBody UserModel user) {
        this.userRepository.save(user);
        return ResponseEntity.ok("ok");
    }
}

