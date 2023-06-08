package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.users.UserAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.models.auth.LoginDto;
import app.Quiz.jwzpQuizappProject.models.auth.RegisterDto;
import app.Quiz.jwzpQuizappProject.models.auth.LoginResponseEntity;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final String timeUnit;
    private final long timeAmount;


    public AuthController(TokenService tokenService, UserService userService, AuthenticationManager authenticationManager, @Value("${jwt.timeunit}") String timeUnit, @Value("${jwt.timeamount}") long timeAmount) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.timeUnit = timeUnit;
        this.timeAmount = timeAmount;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
//        TODO how to authenticate against db when user is not created yet?
//         when var authentication = .. goes wrong user gets saved
//         maybe something like transaction?
        try {
            userService.saveUser(registerDto);
        } catch (UserAlreadyExistsException exception) {
            return new ResponseEntity<>("An account with email: " + registerDto.email() + " already exists.", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponseEntity login(@Valid @RequestBody LoginDto loginDto) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );
        return new LoginResponseEntity(tokenService.generateToken(authentication, timeAmount, timeUnit), timeAmount, timeUnit);
    }

}
