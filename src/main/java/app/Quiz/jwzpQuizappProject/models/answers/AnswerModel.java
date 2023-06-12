package app.Quiz.jwzpQuizappProject.models.answers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
public class AnswerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    int ordNum;
    String text;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    int score;
    @NonNull
    Instant createdAt;
    @JsonIgnore
    long questionId;

    public AnswerModel(int ordNum, String text, int score, @NonNull Instant createdAt, long questionId) {
        this.ordNum = ordNum;
        this.text = text;
        this.score = score;
        this.questionId = questionId;
        this.createdAt = createdAt;
    }

    protected AnswerModel() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    @NonNull
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(@NonNull Instant createdAt) {
        this.createdAt = createdAt;
    }
    public int getOrdNum() {
        return ordNum;
    }
    public void setOrdNum(int ordNum) {
        this.ordNum = ordNum;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
