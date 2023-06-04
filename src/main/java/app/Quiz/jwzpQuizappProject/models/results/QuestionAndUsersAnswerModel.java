package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
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
    @JsonProperty("questionOrdNum")
    long questionOrdNum;            // to jest ordNumber, nie ID! dzieki temu nie musimyu sprawdzac czy question nalezy do quizu

    @ManyToOne
    @JoinColumn(name = "answer")
    AnswerModel answer;

    @Transient
//    @JsonIgnore
    @JsonProperty("userAnswerOrdNum")
    long userAnswerOrdNum;       // to jest ordNumber, nie ID! dzieki temu nie musimyu sprawdzac czy answer nalezy do question

    public QuestionAndUsersAnswerModel() {
        userAnswerOrdNum = -1;      // gdy odpowiednie pola nie beda podane w JSONie, to bedzie mozna
        questionOrdNum = -1;        // to latwo sprawdzic
    }


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

    public AnswerModel getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerModel answer) {
        this.answer = answer;
    }

    public long getUserAnswerOrdNum() {
        return userAnswerOrdNum;
    }

    public void setUserAnswerOrdNum(long userAnswerOrdNum) {
        this.userAnswerOrdNum = userAnswerOrdNum;
    }

    public long getQuestionOrdNum() {
        return questionOrdNum;
    }

    public void setQuestionOrdNum(long questionOrdNum) {
        this.questionOrdNum = questionOrdNum;
    }

    @Override
    public String toString() {
        return "QuestionAndUsersAnswerModel{" +
                "id=" + id +
                ", question=" + question +
                ", questionOrdNum=" + questionOrdNum +
                ", answer=" + answer +
                ", userAnswerOrdNum=" + userAnswerOrdNum +
                '}';
    }
}
