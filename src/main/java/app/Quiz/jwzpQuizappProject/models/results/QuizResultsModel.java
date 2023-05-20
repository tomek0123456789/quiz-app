package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Set;

@Entity
public class QuizResultsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @OneToOne
    @JoinColumn(name = "quiz")
    QuizModel quiz;

    @JsonProperty("quizId")
    long quizId;

    @OneToMany
    Set<QuestionAndUsersAnswerModel> questionsAndAnswers;

    @ManyToMany
    @JsonIgnore
    Set<ResultsModel> results;

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

    public void addQAA(QuestionAndUsersAnswerModel qaa){
        this.questionsAndAnswers.add(qaa);
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public Set<ResultsModel> getResults() {
        return results;
    }

    public void setResults(Set<ResultsModel> results) {
        this.results = results;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
