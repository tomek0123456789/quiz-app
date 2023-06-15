package app.Quiz.jwzpQuizappProject.exceptions;

import app.Quiz.jwzpQuizappProject.config.Constants;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionsLimitException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.InvalidRoomDataException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.exceptions.ExceptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(Constants.LOGGER_NAME);

    @ExceptionHandler({
            AnswerNotFoundException.class,
            CategoryNotFoundException.class,
            QuestionNotFoundException.class,
            QuizNotFoundException.class,
            ResultNotFoundException.class,
            RoomNotFoundException.class,
            UserNotFoundException.class,
    })
    protected ResponseEntity<Object> handleEntityNotFound(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    protected ResponseEntity<Object> handlePermissionDenied(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AnswerAlreadyExists.class,
            CategoryAlreadyExistsException.class,
            UserAlreadyExistsException.class
    })
    protected ResponseEntity<Object> handleEntityAlreadyExists(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            QuestionsLimitException.class,
            InvalidRoomDataException.class
    })
    protected ResponseEntity<Object> handleLimit(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
