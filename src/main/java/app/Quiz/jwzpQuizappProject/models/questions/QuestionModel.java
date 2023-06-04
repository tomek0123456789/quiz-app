package app.Quiz.jwzpQuizappProject.models.questions;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Entity
public class QuestionModel{ // T is type of question, like image or string etc
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String content;
    @NonNull
    Instant createdAt;
    Integer ordNum;
    @OneToMany
    @OrderBy("ordNum ASC")
    List<AnswerModel> answers;

    @JsonIgnore
    long quizId;
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
//    List<QuestionAndUsersAnswerModel> questionAndUsersAnswers;

    public QuestionModel() {
    }
    public QuestionModel(String content, int ordNum, long quizId, @NonNull Instant createdAt) {
        this.content = content;
        this.ordNum = ordNum;
        this.quizId = quizId;
        this.createdAt = createdAt;
        this.answers = Collections.emptyList();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    @NonNull
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(@NonNull Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getOrdNum() {
        return ordNum;
    }
    public void setOrdNum(Integer ordNum) {
        this.ordNum = ordNum;
    }
    public List<AnswerModel> getAnswers() {
        return answers;
    }
    public int getAnswersSize() {
        return answers.size();
    }
    public AnswerModel getSingleAnswerByOrdNum(int ordNum) throws AnswerNotFoundException{
        return answers.stream()
                .filter(ans -> ans.getOrdNum() == ordNum)
                .findFirst()
                .orElseThrow(AnswerNotFoundException::new);
    }
    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }
    public void addAnswer(AnswerModel answer) {
        answers.add(answer);
    }
    public void removeAnswer(AnswerModel answer) {
        answers.remove(answer);
        setOrderNumbers();
    }
    private void setOrderNumbers() {
        for (int i = 0; i < getAnswersSize(); i++) {
            answers.get(i).setOrdNum(i);
        }
    }
//    public List<QuestionAndUsersAnswerModel> getQuestionAndUsersAnswers() {
//        return questionAndUsersAnswers;
//    }
//
//    public void setQuestionAndUsersAnswers(List<QuestionAndUsersAnswerModel> questionAndUsersAnswers) {
//        this.questionAndUsersAnswers = questionAndUsersAnswers;
//    }
}
