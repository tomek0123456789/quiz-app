package app.Quiz.jwzpQuizappProject.models.questions;

import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class QuestionModel{ // T is type of question, like image or string etc

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;

    @NonNull
    LocalDateTime createdAt;

    Integer ordNum;

    @OneToMany
    @OrderBy("ordNum ASC")
    List<AnswerModel> answers;

    public QuestionModel(@NonNull String content) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public QuestionModel() {
        this.createdAt = LocalDateTime.now();
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull LocalDateTime createdAt) {
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

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }

    public void addAnswer(AnswerModel answer) {
        this.answers.add(answer);
        answer.setOrdNum(this.answers.size() - 1);
    }

    public void addAnswerAt(AnswerModel answer, Integer index) {
        this.answers.add(index,answer);

        System.out.println(index);

        for(int i = index; i < this.answers.size(); i++){
            this.answers.get(i).setOrdNum(i);
        }
    }

    public AnswerModel getAnswerByOrdNum(Integer ordNum) {
        return this.answers.get(ordNum);
    }

    public void deleteAnswer(AnswerModel answer) {
        this.answers.remove(answer);
    }
}
