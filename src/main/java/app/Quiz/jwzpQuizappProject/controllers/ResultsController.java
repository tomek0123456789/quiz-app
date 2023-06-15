package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.config.Constants;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.results.*;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/results")
public class ResultsController {
    private final Logger log = LoggerFactory.getLogger(Constants.LOGGER_NAME);
    private final ResultsService resultsService;
    private final TokenService tokenService;

    public ResultsController(ResultsService resultsService, TokenService tokenService) {
        this.resultsService = resultsService;
        this.tokenService = tokenService;
    }

    @GetMapping("/my")
    public List<ResultsModel> getAllUserResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets all his results.");
        return resultsService.getAllMyResults(token);
    }

    @GetMapping("/{id}")
    public ResultsModel getSingleResult(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) throws PermissionDeniedException, ResultNotFoundException {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets results with id: " + id + ".");
        return resultsService.getSingleResult(id, token);
    }

    @GetMapping("/quiz/{id}")
    public Set<QuizResultsModel> getAllUserQuizResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets quiz results for quiz with id: " + id + ".");
        return resultsService.getMyResultsForQuiz(id, token);
    }

    @GetMapping("/quiz/{id}/best-result")
    public QuizResultsModel getMyBestResult(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " gets his best results for quiz with id: " + id + ".");
        return resultsService.getMyBestResultForQuiz(token, id);
    }

    @PostMapping
    public ResponseEntity<ResultsModel> createResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody ResultsDto results
    ) throws AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to create results.");
        var createdResults = resultsService.createResults(results, token);
        log.info("User with email: " + userEmail + " created results, results: " + createdResults + ".");
        return new ResponseEntity<>(createdResults, HttpStatus.CREATED);
    }

    @PatchMapping("/qaa")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateQuestionAndUserAnswer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody QuestionAndUsersAnswerPatchDto questionAndUsersAnswerPatchDto
    ) throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update answer with id: " + questionAndUsersAnswerPatchDto.id() + ".");
        var updatedAnswer = resultsService.updateQuestionAndUsersAnswer(questionAndUsersAnswerPatchDto);
        log.info("User with email: " + userEmail + " updated answer, answer: " + updatedAnswer + ".");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/quiz")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateQuizResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody QuizResultsPatchDto quizResultsPatchDto
    ) throws AnswerNotFoundException, QuizNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update quiz (id: " + quizResultsPatchDto.quizId() + ") results with id: " + quizResultsPatchDto.quizResultsId() + ".");
        var updatedResults = resultsService.updateQuizResults(quizResultsPatchDto);
        log.info("User with email: " + userEmail + " updated quiz (id: " + quizResultsPatchDto.quizId() + ") results, results: " + updatedResults + ".");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody ResultsPatchDto resultsPatchDto
    ) throws RoomNotFoundException, ResultNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to update room (id: " + resultsPatchDto.roomId() + ") results with id: " + resultsPatchDto.resultsId() + ".");
        var updatedResults = resultsService.updateResults(resultsPatchDto);
        log.info("User with email: " + userEmail + " updated room (id: " + resultsPatchDto.roomId() + ") results, results: " + updatedResults + ".");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/quizresults/{quizResultsId}/qaa/{qaaId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteQuestionAndAnswer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizResultsId,
            @PathVariable long qaaId
    ) throws AnswerNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to delete quiz (id: " + quizResultsId + ") answer with id: " + qaaId + ".");
        resultsService.deleteQuestionAndAnswer(qaaId, quizResultsId);
        log.info("User with email: " + userEmail + " deleted quiz (id: " + quizResultsId + ") answer with id: " + qaaId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{resultsId}/quizresults/{quizResultsId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteQuizResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long resultsId,
            @PathVariable long quizResultsId
    ) throws AnswerNotFoundException, ResultNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to delete quiz (id: " + quizResultsId + ") results with id: " + quizResultsId + ".");
        resultsService.deleteQuizResults(quizResultsId, resultsId);
        log.info("User with email: " + userEmail + " deleted quiz (id: " + quizResultsId + ") results with id: " + quizResultsId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{resultsId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long resultsId
    ) throws ResultNotFoundException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to delete results with id: " + resultsId + ".");
        resultsService.deleteResults(resultsId);
        log.info("User with email: " + userEmail + " deleted results with id: " + resultsId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
