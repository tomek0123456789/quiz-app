package app.Quiz.jwzpQuizappProject.exceptions;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionsLimitException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.exceptions.ExceptionDto;
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

    @ExceptionHandler({
            AnswerNotFoundException.class,
            CategoryNotFoundException.class,
            QuestionNotFoundException.class,
            QuizNotFoundException.class,
            ResultNotFoundException.class,
            RoomNotFoundException.class,
            UserNotFoundException.class,
    })
    protected ResponseEntity<Object> handleEntityNotFound(AnswerNotFoundException ex) {
        return new ResponseEntity<>(new ExceptionDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    protected ResponseEntity<Object> handleEntityNotFound(PermissionDeniedException ex) {
        return new ResponseEntity<>(new ExceptionDto(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AnswerAlreadyExists.class,
            CategoryAlreadyExistsException.class,
    })
    protected ResponseEntity<Object> handleEntityNotFound(AnswerAlreadyExists ex) {
        return new ResponseEntity<>(new ExceptionDto(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(QuestionsLimitException.class)
    protected ResponseEntity<Object> handleEntityNotFound(QuestionsLimitException ex) {
        return new ResponseEntity<>(new ExceptionDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
