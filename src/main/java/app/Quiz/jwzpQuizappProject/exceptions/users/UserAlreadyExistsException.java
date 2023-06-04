package app.Quiz.jwzpQuizappProject.exceptions.users;

import javax.naming.AuthenticationException;

public class UserAlreadyExistsException extends AuthenticationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
