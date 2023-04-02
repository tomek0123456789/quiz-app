package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.AnswerRepository;
import app.Quiz.jwzpQuizappProject.repositories.QuestionRepository;
import app.Quiz.jwzpQuizappProject.repositories.QuizRepository;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/quizes")
public class QuizController {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository; //delete once auth system done

    public QuizController(QuizRepository quizRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    //////  QUIZ    //////

    @GetMapping("/{id}")
    public ResponseEntity getSingleQuiz(@PathVariable long id) {
        return ResponseEntity.ok(this.quizRepository.findById(id));
    }

    @GetMapping()
    public ResponseEntity getAllQuizes() {
        return ResponseEntity.ok(this.quizRepository.findAll());
    }

    @PostMapping()
    public ResponseEntity createQuiz(@RequestBody QuizModel newQuiz) {

        // TODO: user should be read from jwt token somehow
        Optional<UserModel> user = userRepository.findById(Long.valueOf(52));
        user.ifPresent(newQuiz::setOwner);

        this.quizRepository.save(newQuiz);

        return ResponseEntity.ok("ok");
    }

    // TODO: check if user is an owner or an admin
    @DeleteMapping("/{id}")
    public ResponseEntity deleteQuiz(@PathVariable long id) {
        this.quizRepository.deleteById(id);

        return ResponseEntity.ok("ok");
    }

    // TODO: check if user is an owner or an admin
    @PutMapping()
    public ResponseEntity updateQuiz(@RequestBody QuizModel quiz) {
        this.quizRepository.save(quiz);

        return ResponseEntity.ok("ok");
    }

    // TODO: rozdzielic to na rozne pliki jednak - nw czy nie bedzie jakis konfliktow z pathem
    //////  QUESTION    //////

    // TODO: check if user is an owner or an admin
    @PostMapping("/{id}/questions")
    public ResponseEntity addQuestionToQuiz(@PathVariable long id, @RequestBody QuestionModel newQuestion) {
        Optional<QuizModel> quiz = this.quizRepository.findById(id);

        this.questionRepository.save(newQuestion);
        quiz.ifPresent(quizModel -> quizModel.addQuestion(newQuestion));
        quiz.ifPresent(this.quizRepository::save);

        return ResponseEntity.ok("added question");
    }

    // TODO: check if user is an owner or an admin
    @PostMapping("/{id}/questions/{ind}")
    public ResponseEntity insertQuestionToQuizAtIndex(@PathVariable long id,@PathVariable Integer ind, @RequestBody QuestionModel newQuestion) {
        Optional<QuizModel> quiz = this.quizRepository.findById(id);

        this.questionRepository.save(newQuestion);
        quiz.ifPresent(quizModel -> quizModel.addQuestionAt(newQuestion, ind));
        quiz.ifPresent(this.quizRepository::save);

        // saving updated ordNums for all questions
        quiz.ifPresent(quizModel -> this.questionRepository.saveAll(quizModel.getQuestions()));

        return ResponseEntity.ok("added question");
    }

    // TODO: check if user is an owner or an admin
    @DeleteMapping("/{quizId}/questions/{ordNum}")
    public ResponseEntity deleteQuestionFromQuiz(@PathVariable long quizId, @PathVariable Integer ordNum) {
        Optional<QuizModel> quiz = this.quizRepository.findById(quizId);

        if (quiz.isPresent()) {
            QuestionModel question = quiz.get().getQuestionByOrdNum(ordNum);
            quiz.get().deleteQuestion(question);
            this.quizRepository.save(quiz.get());
            this.questionRepository.delete(question);
        }

        return ResponseEntity.ok("question deleted");
    }

    //////  ANSWER    //////
    // TODO: check if user is an owner or an admin
    @PostMapping("/{questionId}/questions/{ordNum}/answers")
    public ResponseEntity addAnswerToQuestionInQuiz(@PathVariable long questionId,@PathVariable Integer ordNum, @RequestBody AnswerModel newAnswer) {
        Optional<QuizModel> quiz = this.quizRepository.findById(questionId);

        if(quiz.isPresent()){
            this.answerRepository.save(newAnswer);
            QuestionModel question = quiz.get().getQuestionByOrdNum(ordNum);
            question.addAnswer(newAnswer);
            this.questionRepository.save(question);
        }

        return ResponseEntity.ok("added answer");
    }

    @DeleteMapping("/questions/{questionId}/answers/{answerOrdNum}")
    public ResponseEntity deleteAnswerFromQuestion( @PathVariable Long questionId, @PathVariable Integer answerOrdNum) {
        Optional<QuestionModel> question = this.questionRepository.findById(questionId);

        if (question.isPresent()) {
            AnswerModel answer =  question.get().getAnswerByOrdNum(answerOrdNum);

            question.get().deleteAnswer(answer);

            this.questionRepository.save(question.get());
            this.answerRepository.delete(answer);
        }

        return ResponseEntity.ok("question deleted");
    }


}
