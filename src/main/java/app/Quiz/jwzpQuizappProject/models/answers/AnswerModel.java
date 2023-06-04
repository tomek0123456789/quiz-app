package app.Quiz.jwzpQuizappProject.models.answers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class AnswerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    String text;
    @NonNull
    Instant createdAt;
    Integer ordNum;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Integer score;

    public AnswerModel(String text, int score, int ordNum, @NonNull Instant createdAt) {
        this.text = text;
        this.score = score;
        this.ordNum = ordNum;
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
    public Integer getOrdNum() {
        return ordNum;
    }
    public void setOrdNum(Integer ordNum) {
        this.ordNum = ordNum;
    }
    public Integer getScore() {
        return score == null ? 0 : score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
}
