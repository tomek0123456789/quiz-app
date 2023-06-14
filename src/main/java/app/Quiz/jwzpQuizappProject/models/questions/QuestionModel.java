package app.Quiz.jwzpQuizappProject.models.questions;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "questions")
public class QuestionModel{ // T is type of question, like image or string etc
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    int ordNum;
    private String content;
    QuestionStatus status;
    @NonNull
    Instant createdAt;
    @OneToMany(mappedBy = "questionId")
    @OrderBy("ordNum ASC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<AnswerModel> answers;
    @JsonIgnore
    long quizId;

    public QuestionModel(int ordNum, String content, @NonNull Instant createdAt, long quizId) {
        this.ordNum = ordNum;
        this.content = content;
        this.status = QuestionStatus.INVALID;
        this.createdAt = createdAt;
        this.answers = Collections.emptyList();
        this.quizId = quizId;
    }
    protected QuestionModel() {}

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
    public QuestionStatus getQuestionStatus() {
        return status;
    }
    public void setQuestionStatus(QuestionStatus status) {
        this.status = status;
    }
    public int getOrdNum() {
        return ordNum;
    }
    public void setOrdNum(int ordNum) {
        this.ordNum = ordNum;
    }
    public List<AnswerModel> getAnswers() {
        return answers;
    }
    public int answersSize() {
        return answers.size();
    }
    public int nextAnswerOrdinalNumber() {
        return answers.size() + 1;
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
        setAnswersOrderNumbers();
    }
    private void setAnswersOrderNumbers() {
        for (int i = 0; i < answersSize(); i++) {
            answers.get(i).setOrdNum(i + 1);
        }
    }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "id=" + id +
                ", ordNum=" + ordNum +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", answers=" + answers +
                ", quizId=" + quizId +
                '}';
    }
}
