package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Entity
public class ResultsModel{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany
    private Set<QuizResultsModel> quizzesResults;

    @ManyToOne
    UserModel owner;

    @NonNull
    LocalDateTime createdAt;

    @ManyToOne
    RoomModel room;


    long score;

    @JsonCreator
    public ResultsModel(@JsonProperty("quizzesResults") Set<QuizResultsModel> quizesResults) {
        this.quizzesResults = quizesResults;
        this.createdAt = LocalDateTime.now();
    }

    public ResultsModel() {
        this.createdAt = LocalDateTime.now();
        this.quizzesResults = Collections.emptySet();
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull LocalDateTime createdAt) {
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
