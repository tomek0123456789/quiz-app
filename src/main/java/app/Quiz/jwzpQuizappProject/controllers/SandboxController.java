package app.Quiz.jwzpQuizappProject.controllers;


// uzywam do testowania nowych rzeczy, zeby nie smiecic innych plików

import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.QuestionAndUsersAnswerModel;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/sandbox")
public class SandboxController {

    final QuestionRepository questionRepository;

    final QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    final QuizResultsRepository quizResultsRepository;
    final ResultsRepository resultsRepository;

    final QuizRepository quizRepository;

    public SandboxController(QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository, QuestionRepository questionRepository, QuizResultsRepository quizResultsRepository, ResultsRepository resultsRepository, QuizRepository quizRepository) {
        this.questionAndUsersAnswerRepository = questionAndUsersAnswerRepository;
        this.questionRepository = questionRepository;
        this.quizResultsRepository = quizResultsRepository;
        this.resultsRepository = resultsRepository;
        this.quizRepository = quizRepository;
    }

    @PostMapping
    public ResponseEntity createQaa(@RequestBody QuestionAndUsersAnswerModel newQaa) {
        QuestionModel question = questionRepository.findById(newQaa.getQuestionId()).orElse(null);
        if (question == null) {
            return ResponseEntity.badRequest().body("Invalid question ID");
        }
        newQaa.setQuestion(question);
        QuestionAndUsersAnswerModel createdQaa = questionAndUsersAnswerRepository.save(newQaa);
        return ResponseEntity.ok(createdQaa);
    }


    @PostMapping("/results")
    public ResponseEntity createResults(@RequestBody ResultsModel newResults) {

        for (QuizResultsModel quizResult : newResults.getQuizesResults()) {
            Optional<QuizModel> quiz = this.quizRepository.findById(quizResult.getQuizId());

            if(quiz.isEmpty()){
                return ResponseEntity.badRequest().body("Invalid quiz ID");
            }

            quizResult.setQuiz(quiz.get());

            for (QuestionAndUsersAnswerModel qaa : quizResult.getQuestionsAndAnswers()) {
                Optional<QuestionModel> question = this.questionRepository.findById(qaa.getQuestionId());

                if(question.isEmpty()){
                    return ResponseEntity.badRequest().body("Invalid question ID");
                }

                qaa.setQuestion(question.get());

                this.questionAndUsersAnswerRepository.save(qaa);
            }

//            this.quizResultsRepository.save(quizResult);
        }

//        resultsRepository.save(newResults);


        return ResponseEntity.ok(newResults);
    }
}
