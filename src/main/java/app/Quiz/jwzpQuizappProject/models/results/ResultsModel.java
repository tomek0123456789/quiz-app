package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class ResultsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany
    private Set<QuizResultsModel> quizesResults;

    @ManyToOne
    UserModel owner;

    @NonNull
    LocalDateTime createdAt;


    long score;

    @JsonCreator
    public ResultsModel(@JsonProperty("quizesResults") Set<QuizResultsModel> quizesResults) {
        this.quizesResults = quizesResults;
        this.createdAt = LocalDateTime.now();
    }

    public ResultsModel() {this.createdAt = LocalDateTime.now();}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<QuizResultsModel> getQuizesResults() {
        return quizesResults;
    }

    public void setQuizesResults(Set<QuizResultsModel> quizesResults) {
        this.quizesResults = quizesResults;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @NonNull
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
