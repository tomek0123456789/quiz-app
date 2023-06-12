package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.results.*;
import app.Quiz.jwzpQuizappProject.repositories.*;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
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
    final QuestionRepository questionRepository;
    final QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    final QuizResultsRepository quizResultsRepository;
    final ResultsRepository resultsRepository;
    final QuizRepository quizRepository;
    final AnswerRepository answerRepository;
    final ResultsService resultsService;

    public ResultsController(QuestionRepository questionRepository, QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository, QuizResultsRepository quizResultsRepository, ResultsRepository resultsRepository, QuizRepository quizRepository, AnswerRepository answerRepository, ResultsService resultsService) {
        this.questionRepository = questionRepository;
        this.questionAndUsersAnswerRepository = questionAndUsersAnswerRepository;
        this.quizResultsRepository = quizResultsRepository;
        this.resultsRepository = resultsRepository;
        this.quizRepository = quizRepository;
        this.answerRepository = answerRepository;
        this.resultsService = resultsService;
    }

    @GetMapping("/my")
    public List<ResultsModel> getMyResultsForQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        return resultsService.getAllMyResults(token);
    }

    @GetMapping("/{id}")
    public ResultsModel getSingleResult(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) throws PermissionDeniedException, ResultNotFoundException {
        return resultsService.getSingleResult(id, token);
    }

    @GetMapping("/quiz/{id}")
    public Set<QuizResultsModel> getMyResultsForQuizEndpoint(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) {
        return resultsService.getMyResultsForQuiz(id, token);
    }

    @GetMapping("/quiz/{id}/best-result")
    public ResponseEntity<QuizResultsModel> getMyBestResult(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id
    ) {
        return ResponseEntity.ok(resultsService.getMyBestResultForQuiz(token, id));
    }

    @PostMapping()
    public ResponseEntity<?> createResults(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody ResultsDto results
    ) throws AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists {
        return ResponseEntity.ok(resultsService.createResults(results, token));
    }

    @PatchMapping("/qaa")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateQaa(
            @RequestBody QuestionAndUsersAnswerPatchDto questionAndUsersAnswerPatchDto
    ) throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException {
        resultsService.updateQuestionAndUsersAnswer(questionAndUsersAnswerPatchDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/quiz")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateQuizResults(
            @RequestBody QuizResultsPatchDto quizResultsPatchDto
    ) throws AnswerNotFoundException, QuizNotFoundException {
        resultsService.updateQuizResults(quizResultsPatchDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateResults(@RequestBody ResultsPatchDto resultsPatchDto) throws RoomNotFoundException, ResultNotFoundException {
        resultsService.updateResults(resultsPatchDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/quizresults/{quizResultsId}/qaa/{qaaId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteQuestionAndAnswer(
            @PathVariable long quizResultsId,
            @PathVariable long qaaId
    ) throws AnswerNotFoundException {
        resultsService.deleteQuestionAndAnswer(qaaId, quizResultsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{resultsId}/quizresults/{quizResultsId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteQuizResults(
            @PathVariable long resultsId,
            @PathVariable long quizResultsId
    ) throws AnswerNotFoundException, ResultNotFoundException {
        resultsService.deleteQuizResults(quizResultsId, resultsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{resultsId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteResults(
            @PathVariable long resultsId
    ) throws ResultNotFoundException {
        resultsService.deleteResults(resultsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
