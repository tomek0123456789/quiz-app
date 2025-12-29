package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.config.Constants;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswersLimitException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionsLimitException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerDto;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionDto;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizPatchDto;
import app.Quiz.jwzpQuizappProject.service.QuizService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private final Logger log = LoggerFactory.getLogger(Constants.LOGGER_NAME);
    private final TokenService tokenService;
    private final QuizService quizService;

    public QuizController(TokenService tokenService, QuizService quizService) {
        this.tokenService = tokenService;
        this.quizService = quizService;
    }

    //////  QUIZ    //////

    @GetMapping("/{quizId}")
    public QuizModel getSingleQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId
    ) throws QuizNotFoundException {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets quiz with id: " + quizId + ".");
        return quizService.getSingleQuiz(quizId, token);
    }

    @GetMapping
    public List<QuizModel> getMultipleQuizzes(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(value = "name", required = false) Optional<String> titlePart,
            @RequestParam(value = "category", required = false) Optional<String> categoryName
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets quizzes with parameters: " +
                "{titlePart: " + titlePart.orElse("\"\"") +
                ", categoryName: " + categoryName.orElse("\"\"") + "}.");
        return quizService.getMultipleQuizzes(titlePart, categoryName, token);
    }

    @GetMapping("/my")
    public List<QuizModel> getMyQuizzes(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets his quizzes.");
        return quizService.getUserQuizzes(token);
    }

    @PostMapping
    public ResponseEntity<QuizModel> createQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody QuizDto quizDto
    ) throws CategoryNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to create a quiz.");
        var quiz = quizService.addQuiz(quizDto, token);
        log.info("User with email: " + userEmail + " created a quiz: " + quiz + ".");
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId
    ) throws QuizNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to delete a quiz with id: " + quizId + ".");
        quizService.deleteQuiz(quizId, token);
        log.info("User with email: " + userEmail + " deleted a quiz with id: " + quizId + ".");
        return new ResponseEntity<>("Successfully deleted a quiz with id: " + quizId + ".", HttpStatus.NO_CONTENT);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public QuizModel updateQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody QuizModel quiz
    ) throws CategoryNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update a quiz with id: " + quiz.getId() + ".");
        var updatedQuiz = quizService.updateQuiz(quiz);
        log.info("User with email: " + userEmail + " updated a quiz with id: " + quiz.getId() + "to: " + updatedQuiz + ".");
        return updatedQuiz;
    }

    @PatchMapping("/{quizId}")
    public QuizModel patchQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @RequestBody QuizPatchDto quizPatchDto
    ) throws QuizNotFoundException, CategoryNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update a quiz with id: " + quizId + ".");
        var updatedQuiz = quizService.updateQuiz(quizId, quizPatchDto, token);
        log.info("User with email: " + userEmail + " updated a quiz with id: " + quizId + "to: " + updatedQuiz + ".");
        return updatedQuiz;
    }

    @PatchMapping("/{quizId}/validate")
    public QuizModel validateQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId
    ) throws QuizNotFoundException, PermissionDeniedException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to *validate* a quiz with id: " + quizId + ".");
        var validatedQuiz = quizService.validateQuiz(quizId, token);
        log.info("User with email: " + userEmail + " *validated* a quiz with id: " + quizId + ".");
        return validatedQuiz;
    }

    //////  QUESTION    //////

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestionToQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @RequestBody QuestionDto questionDto
    ) throws QuizNotFoundException, PermissionDeniedException, QuestionsLimitException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to add a question to a quiz with id: " + quizId + ".");
        QuestionModel model = quizService.addQuestionToQuiz(quizId, questionDto, token);
        log.info("User with email: " + userEmail + " added a question to a quiz with id: " + quizId + ", question: " + model + ".");
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @DeleteMapping("/{quizId}/questions/{questionOrdNum}")
    public ResponseEntity<String> removeQuestionFromQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @PathVariable int questionOrdNum
    ) throws QuizNotFoundException, QuestionNotFoundException, PermissionDeniedException, QuestionsLimitException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to remove a question with ordNum: " + questionOrdNum + " from a quiz with id: " + quizId + ".");
        quizService.removeQuestionFromQuiz(quizId, questionOrdNum, token);
        log.info("User with email: " + userEmail + " removed a question with ordNum: " + questionOrdNum + " from a quiz with id: " + quizId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //////  ANSWER    //////
    @PostMapping("/{quizId}/questions/{questionOrdNum}/answers")
    public ResponseEntity<AnswerModel> addAnswerToQuestion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @PathVariable int questionOrdNum,
            @RequestBody AnswerDto answerDto
    ) throws QuizNotFoundException, QuestionNotFoundException, PermissionDeniedException, AnswersLimitException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to add an answer to a question with ordNum: " + questionOrdNum + " in a quiz with id: " + quizId + ".");
        AnswerModel answer = quizService.addAnswerToQuestion(quizId, questionOrdNum, answerDto, token);
        log.info("User with email: " + userEmail + " added an answer to a question with ordNum: " + questionOrdNum + " in a quiz with id: " + quizId + ", answer: " + answer + ".");
        return new ResponseEntity<>(answer, HttpStatus.CREATED);
    }

    // todo maybe patch?
    @DeleteMapping("/{quizId}/questions/{questionOrdNum}/answers/{answerOrdNum}")
    public ResponseEntity<String> removeAnswerFromQuestion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @PathVariable int questionOrdNum,
            @PathVariable int answerOrdNum
    ) throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException, PermissionDeniedException, AnswersLimitException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to delete an answer to a question with ordNum: " + questionOrdNum + " in a quiz with id: " + quizId + ".");
        quizService.removeAnswerFromQuestion(quizId, questionOrdNum, answerOrdNum, token);
        log.info("User with email: " + userEmail + " deleted an answer to a question with ordNum: " + questionOrdNum + " in a quiz with id: " + quizId + ".");
        return new ResponseEntity<>("Successfully deleted an answer no. " + answerOrdNum + " from a question no. " + questionOrdNum + " from a quiz with id: " + quizId + ".", HttpStatus.NO_CONTENT);
    }
}
