package app.Quiz.jwzpQuizappProject.exceptions.users;

public class UserNotFoundException extends Exception{
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
