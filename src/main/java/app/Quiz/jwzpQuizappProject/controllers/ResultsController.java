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
    public ResponseEntity<List<ResultsModel>> getMyResultsForQuiz(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return ResponseEntity.ok(this.resultsService.getAllMyResults(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingleResult(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                          @PathVariable long id) throws PermissionDeniedException, ResultNotFoundException {
        return ResponseEntity.ok(this.resultsService.getSingleResult( id, token));
    }

    @GetMapping("/quiz/{id}")
    public ResponseEntity<Set<QuizResultsModel>> getMyResultsForQuizEndpoint(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                                             @PathVariable long id){
        return ResponseEntity.ok( resultsService.getMyResultsForQuiz(token,id));
    }

    @GetMapping("/quiz/{id}/best-result")
    public ResponseEntity<QuizResultsModel> getMyBestResult(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long id){
        return ResponseEntity.ok(resultsService.getMyBestResultForQuiz(token, id));
    }

    @PostMapping()
    public ResponseEntity<?> createResults(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                        @RequestBody ResultsDto results) throws AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists {
        return ResponseEntity.ok( this.resultsService.createResults(results, token));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/qaa")
    public ResponseEntity<?> updateQaa(@RequestBody QuestionAndUsersAnswerPatchDto questionAndUsersAnswerPatchDto) throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException {
        this.resultsService.updateQuestionAndUsersAnswer(questionAndUsersAnswerPatchDto);
        return ResponseEntity.ok("");
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/quiz")
    public ResponseEntity<?> updateQuizResults(@RequestBody QuizResultsPatchDto quizResultsPatchDto) throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException {
        this.resultsService.updateQuizResults(quizResultsPatchDto);
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping
    public ResponseEntity<?> updateResults(@RequestBody ResultsPatchDto resultsPatchDto) throws RoomNotFoundException, ResultNotFoundException {
        this.resultsService.updateResults(resultsPatchDto);
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/quizresults/{quizResultsId}/qaa/{qaaId}")
    public ResponseEntity<?> deleteQuestionAndAnswer(@PathVariable long quizResultsId,
                                                  @PathVariable long qaaId) throws AnswerNotFoundException {
        this.resultsService.deleteQuestionAndAnswer(qaaId, quizResultsId);
        return ResponseEntity.ok("");
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{resultsId}/quizresults/{quizResultsId}")
    public ResponseEntity<?> deleteQuizResults(@PathVariable long resultsId,
                                                     @PathVariable long quizResultsId
    ) throws AnswerNotFoundException, ResultNotFoundException {
        this.resultsService.deleteQuizResults(quizResultsId,resultsId);
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{resultsId}")
    public ResponseEntity<?> deleteResults(@PathVariable long resultsId
    ) throws ResultNotFoundException {
        this.resultsService.deleteResults(resultsId);
        return ResponseEntity.ok("");
    }
}
