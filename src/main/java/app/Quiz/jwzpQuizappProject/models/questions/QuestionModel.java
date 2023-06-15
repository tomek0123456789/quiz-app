package app.Quiz.jwzpQuizappProject.models.questions;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static app.Quiz.jwzpQuizappProject.config.Constants.VALID_QUESTION_ANSWERS_SIZE_LIMIT;

@Entity
@Table(name = "questions")
public class QuestionModel { // T is type of question, like image or string etc
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
        this.answers = new ArrayList<>(2);
        this.quizId = quizId;
    }

    public QuestionModel() {
        this.answers = new ArrayList<>(2);
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

    public AnswerModel getSingleAnswerByOrdNum(int ordNum) throws AnswerNotFoundException {
        return answers.stream()
                .filter(ans -> ans.getOrdNum() == ordNum)
                .findFirst()
                .orElseThrow(AnswerNotFoundException::new);
    }

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
        updateQuestion();
    }

    public void addAnswer(AnswerModel answer) {
        answers.add(answer);
        updateQuestion();
    }

    public void removeAnswer(AnswerModel answer) {
        answers.remove(answer);
        updateQuestion();
    }

    private void updateQuestion() {
        setAnswersOrderNumbers();
        updateQuestionStatus();
    }

    private void setAnswersOrderNumbers() {
        for (int i = 0; i < answersSize(); i++) {
            answers.get(i).setOrdNum(i + 1);
        }
    }

    private void updateQuestionStatus() {
        if (answersSize() >= VALID_QUESTION_ANSWERS_SIZE_LIMIT) {
            status = QuestionStatus.VALID;
        } else {
            status = QuestionStatus.INVALID;
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
