package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public QuizResultsModel() {}

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

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }
}
