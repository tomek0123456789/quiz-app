package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/results")
public class ResultsController {

    final QuestionRepository questionRepository;
    final QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    final QuizResultsRepository quizResultsRepository;
    final ResultsRepository resultsRepository;
    final QuizRepository quizRepository;
    final AnswerRepository answerRepository;
    private final ResultsService resultsService;
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
    public ResponseEntity getMyResults(){
        // should return all results where owner == curr user
        return ResponseEntity.ok( this.resultsRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity getSingleResult(@PathVariable long id){
        // should return all results where owner == curr user
        return ResponseEntity.ok( this.resultsRepository.findById(id));
    }



    @GetMapping("/quiz/{id}")
    public ResponseEntity getMyResultsEndpoint(@PathVariable long id){
        return ResponseEntity.ok( resultsService.getMyResults(id));
    }


    @GetMapping("/quiz/{id}/best-result")
    public ResponseEntity getMyBestResult(@PathVariable long id){

        QuizResultsModel bestResult = null;
        for(QuizResultsModel quizResult : resultsService.getMyResults(id)){
            if(bestResult == null || quizResult.getScore() > bestResult.getScore()){
                bestResult = quizResult;
            }
        }

        return ResponseEntity.ok(bestResult);
    }



    // TODO: serwise chyyba powinien zwracac ResultsModel (a nie ResponseEntity)
    //       tylko nie jestem pewny jak wtedy handlować jakieś errory (badReqesty)
    @PostMapping()
    public ResponseEntity createResults(@RequestBody ResultsModel newResults) {
        return ResponseEntity.ok(resultsService.createResults(newResults));
    }


}
