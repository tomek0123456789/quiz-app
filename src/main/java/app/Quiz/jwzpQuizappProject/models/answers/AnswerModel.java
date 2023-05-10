package app.Quiz.jwzpQuizappProject.models.answers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Entity
public class AnswerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String text;

    @NonNull
    LocalDateTime createdAt;

    Integer ordNum;

    @JsonIgnore
    Integer score;

    public AnswerModel(String text) {
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    public AnswerModel() {
        this.createdAt = LocalDateTime.now();
        this.score = 0;
    }

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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
