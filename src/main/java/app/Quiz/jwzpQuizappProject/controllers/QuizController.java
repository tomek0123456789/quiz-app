package app.Quiz.jwzpQuizappProject.controllers;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin        // to allow frontend-backend connections
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    //////  QUIZ    //////

    @GetMapping("/{quizId}")
    public QuizModel getSingleQuiz(@PathVariable long quizId) throws QuizNotFoundException {
        return quizService.getSingleQuiz(quizId);
    }

    @GetMapping
    public List<QuizModel> getMultipleQuizzes(
            @RequestParam(value = "name", required = false) Optional<String> titlePart,
            @RequestParam(value = "category", required = false) Optional<String> categoryName,
            @RequestParam(value = "valid", required = false) Optional<Boolean> validQuizzes
    ) {
        return quizService.getMultipleQuizzes(titlePart, categoryName, validQuizzes);
    }

    @GetMapping("/my")
    public List<QuizModel> getMyQuizzes(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return quizService.getUserQuizzes(token);
    }

    @PostMapping
    public QuizModel createQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody QuizDto quizDto
    ) throws CategoryNotFoundException {
        return quizService.addQuiz(quizDto, token);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId
    ) throws QuizNotFoundException, PermissionDeniedException {
        quizService.deleteQuiz(quizId, token);
        return new ResponseEntity<>("Successfully deleted a quiz with id: " + quizId + ".", HttpStatus.NO_CONTENT);
    }
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public QuizModel updateQuiz(
            @RequestBody QuizModel quiz
    ) throws CategoryNotFoundException {
        return quizService.updateQuiz(quiz);
    }

    @PatchMapping("/{quizId}")
    public QuizModel patchQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @RequestBody QuizPatchDto quizPatchDto
    ) throws QuizNotFoundException, CategoryNotFoundException, PermissionDeniedException {
        return quizService.updateQuiz(quizId, quizPatchDto, token);
    }

    //////  QUESTION    //////

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestionToQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @RequestBody QuestionDto questionDto
    ) throws QuizNotFoundException, PermissionDeniedException, QuestionsLimitException {
        QuestionModel model = quizService.addQuestionToQuiz(quizId, questionDto, token);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @DeleteMapping("/{quizId}/questions/{questionOrdNum}")
    public ResponseEntity<String> removeQuestionFromQuiz(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @PathVariable int questionOrdNum
    ) throws QuizNotFoundException, QuestionNotFoundException, PermissionDeniedException, QuestionsLimitException {
        quizService.removeQuestionFromQuiz(quizId, questionOrdNum, token);
        return new ResponseEntity<>("Successfully deleted a question no. " + questionOrdNum + " from a quiz with id: " + quizId + ".", HttpStatus.NO_CONTENT);
    }

    //////  ANSWER    //////
    @PostMapping("/{quizId}/questions/{questionOrdNum}/answers")
    public ResponseEntity<AnswerModel> addAnswerToQuestion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long quizId,
            @PathVariable int questionOrdNum,
            @RequestBody AnswerDto answerDto
    ) throws QuizNotFoundException, QuestionNotFoundException, PermissionDeniedException, AnswersLimitException {
        AnswerModel answer = quizService.addAnswerToQuestion(quizId, questionOrdNum, answerDto, token);
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
        quizService.removeAnswerFromQuestion(quizId, questionOrdNum, answerOrdNum, token);
        return new ResponseEntity<>("Successfully deleted an answer no. " + answerOrdNum + " from a question no. " + questionOrdNum + " from a quiz with id: " + quizId + ".", HttpStatus.NO_CONTENT);
    }


}
