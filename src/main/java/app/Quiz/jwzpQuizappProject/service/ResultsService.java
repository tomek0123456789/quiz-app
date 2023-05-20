package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.QuestionAndUsersAnswerModel;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ResultsService {

    final QuestionRepository questionRepository;
    final QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    final QuizResultsRepository quizResultsRepository;
    final ResultsRepository resultsRepository;
    final QuizRepository quizRepository;
    final AnswerRepository answerRepository;

    public ResultsService(QuestionRepository questionRepository, QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository, QuizResultsRepository quizResultsRepository, ResultsRepository resultsRepository, QuizRepository quizRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.questionAndUsersAnswerRepository = questionAndUsersAnswerRepository;
        this.quizResultsRepository = quizResultsRepository;
        this.resultsRepository = resultsRepository;
        this.quizRepository = quizRepository;
        this.answerRepository = answerRepository;
    }


    public Set<QuizResultsModel> getMyResults(long id){
        List<ResultsModel> results = this.resultsRepository.findAll();

        Set<QuizResultsModel> resultsWithQuiz = new HashSet<>();

        for(ResultsModel result : results){
            for (QuizResultsModel quizResult : result.getQuizesResults()) {
                if(quizResult.getQuiz().getId() == id){
                    resultsWithQuiz.add(quizResult);
                    break;
                }
            }

        }

        return  resultsWithQuiz;
    }

    public ResponseEntity createResults(ResultsModel newResults) {
        long roomScore = 0;
        for (QuizResultsModel quizResult : newResults.getQuizesResults()) {
            Optional<QuizModel> quiz = this.quizRepository.findById(quizResult.getQuizId());

            if (quiz.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid quiz ID");
            }

            quizResult.setQuiz(quiz.get());

            Set<QuestionAndUsersAnswerModel> qaaSet = new HashSet<>();

            long quizScore = 0;
            for (QuestionAndUsersAnswerModel qaa : quizResult.getQuestionsAndAnswers()) {
                int questionOrdNum = (int) qaa.getQuestionOrdNum();

                if (questionOrdNum >= quiz.get().getQuestions().size() || questionOrdNum <0) {
                    return ResponseEntity.badRequest().body("Question ord num out of bounds or not provided: " + questionOrdNum + " " + quizResult.getQuizId());
                }

                Optional<QuestionModel> question = Optional.ofNullable(quiz.get().getQuestionByOrdNum(questionOrdNum));


                if (question.isEmpty()) {
                    return ResponseEntity.badRequest().body("Invalid question ID " + questionOrdNum);
                }

                boolean alreadyContainsQuestion = qaaSet.stream()
                        .anyMatch(tempQaa -> tempQaa.getQuestionOrdNum() == questionOrdNum);

                if(alreadyContainsQuestion){
                    return ResponseEntity.badRequest().body("Repeated question!");
                }

                int ansOrdNum = (int) qaa.getUserAnswerOrdNum();

                System.out.println( question.get().getId());

                if (ansOrdNum >= question.get().getAnswers().size() || ansOrdNum <0) {
                    return ResponseEntity.badRequest().body("Answer ord num out of bounds or not provided " + ansOrdNum + " " + questionOrdNum + " " + quizResult.getQuizId());
                }

                Optional<AnswerModel> answer = Optional.ofNullable(question.get().getAnswerByOrdNumber(ansOrdNum));

                if (answer.isEmpty() || ansOrdNum >= question.get().getAnswers().size() || ansOrdNum <0) {
                    return ResponseEntity.badRequest().body("Invalid answer ord num");
                }

                qaa.setQuestion(question.get());
                qaa.setAnswer(answer.get());

                quizScore += answer.get().getScore();

                this.questionAndUsersAnswerRepository.save(qaa);

                qaaSet.add(qaa);
            }
            quizResult.setScore(quizScore);
            roomScore += quizScore;

            this.quizResultsRepository.save(quizResult);
        }

        newResults.setScore(roomScore);
        this.resultsRepository.save(newResults);

        return ResponseEntity.ok(newResults);
    }


}
