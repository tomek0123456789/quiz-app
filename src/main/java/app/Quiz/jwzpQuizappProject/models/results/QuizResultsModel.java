package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class QuizResultsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne
    @JoinColumn(name = "quiz")
    QuizModel quiz;

    @JsonProperty("quizId")
    long quizId;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST)
    Set<QuestionAndUsersAnswerModel> questionsAndAnswers;

//    @ManyToMany
//    @JsonIgnore
//    Set<ResultsModel> results;

    long score;

    public QuizResultsModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public QuizModel getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
    }

    public Set<QuestionAndUsersAnswerModel> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(Set<QuestionAndUsersAnswerModel> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }

    public void deleteQuestionsAndAnswers(QuestionAndUsersAnswerModel qaa){
        this.questionsAndAnswers.remove(qaa);
    }

    public void addQAA(QuestionAndUsersAnswerModel qaa){
        this.questionsAndAnswers.add(qaa);
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

//    public Set<ResultsModel> getResults() {
//        return results;
//    }
//
//    public void setResults(Set<ResultsModel> results) {
//        this.results = results;
//    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void update(QuizResultsPatchDto quizResultsPatchDto, QuizModel quizModel){
        this.quizId = quizResultsPatchDto.quizId() != null ? quizResultsPatchDto.quizId() : this.quizId;
        this.quiz = quizModel != null ? quizModel : this.quiz;
        this.score = quizResultsPatchDto.score() != null ? quizResultsPatchDto.score() : this.score;
    }

    @Override
    public String toString() {
        return "QuizResultsModel{" +
                "id=" + id +
                ", quiz=" + quiz +
                ", quizId=" + quizId +
                ", questionsAndAnswers=" + questionsAndAnswers +
//                ", results=" + results +
                ", score=" + score +
                '}';
    }
}
