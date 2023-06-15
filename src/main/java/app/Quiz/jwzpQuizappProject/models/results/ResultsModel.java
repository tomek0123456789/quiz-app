package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ResultsModel{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany
    private Set<QuizResultsModel> quizzesResults;
    @ManyToOne
    UserModel owner;
    @NonNull
    Instant createdAt;
    @ManyToOne
    RoomModel room;
    long score;

    @JsonCreator
    public ResultsModel(@JsonProperty("quizzesResults") Set<QuizResultsModel> quizesResults) {
        this.quizzesResults = quizesResults;
        this.createdAt = Instant.now();
    }

    public ResultsModel() {
        this.createdAt = Instant.now();
        this.quizzesResults = Collections.emptySet();
    }

    public ResultsModel(Instant createdAt, UserModel owner) {
        this.createdAt = createdAt;
        this.owner = owner;
        this.quizzesResults = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<QuizResultsModel> getQuizzesResults() {
        return quizzesResults;
    }

    public void removeQuizResults(QuizResultsModel quizResults) {
        quizzesResults.remove(quizResults);
    }

    public void setQuizzesResults(Set<QuizResultsModel> quizesResults) {
        this.quizzesResults = quizesResults;
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
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ResultsModel{" +
                "id=" + id +
                ", quizzesResults=" + quizzesResults +
                ", owner=" + owner +
                ", createdAt=" + createdAt +
                ", score=" + score +
                '}';
    }

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public void update(ResultsPatchDto resultsPatchDto, UserModel owner,RoomModel room){
        this.owner = owner != null ? owner : this.owner;
        this.room = room != null ? room : this.room;
        this.score = resultsPatchDto.score() != null ? resultsPatchDto.score() : this.score;
    }
}
