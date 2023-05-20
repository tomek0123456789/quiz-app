package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.UserAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.models.auth.LoginRequest;
import app.Quiz.jwzpQuizappProject.models.auth.RegisterRequest;
import app.Quiz.jwzpQuizappProject.models.responseEntities.LoginResponseEntity;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(TokenService tokenService, UserService userService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
//        TODO how to authenticate against db when user is not created yet?
//         when var authentication = .. goes wrong user gets saved
//         maybe something like transaction?
        try {
            userService.saveUser(registerRequest);
        } catch (UserAlreadyExistsException exception) {
            return new ResponseEntity<>("An account with email: " + registerRequest.email() + " already exists.", HttpStatus.CONFLICT);
        }
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.email(), registerRequest.password())
        );
        return new ResponseEntity<>(tokenService.generateToken(authentication), HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );


        LoginResponseEntity token = new LoginResponseEntity(tokenService.generateToken(authentication));

        return ResponseEntity.ok(token);
    }

}
