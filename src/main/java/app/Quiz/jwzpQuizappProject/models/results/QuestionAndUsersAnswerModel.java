package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class QuestionAndUsersAnswerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne
    @JoinColumn(name = "question")
    QuestionModel question;

    @Transient
//    @JsonIgnore
    @JsonProperty("questionId")
    long questionId;

    public QuestionAndUsersAnswerModel() {}


    public void setQuestion(QuestionModel question) {
        this.question = question;
    }
    public QuestionModel getQuestion() {
        return question;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getQuestionId() {
        return questionId;
    }

}
