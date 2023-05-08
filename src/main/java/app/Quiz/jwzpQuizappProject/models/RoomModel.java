package app.Quiz.jwzpQuizappProject.models;

import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
//import org.hibernate.internal.util.collections.Stack;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Stack;

@Entity
public class RoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String name;

    @ManyToMany
    Set<UserModel> participants;

    @ManyToOne
    UserModel owner;

    LocalDateTime startTime;
//    @Future
//    LocalDateTime endTime // TODO ask majkel

    @ManyToMany(fetch = FetchType.LAZY,  cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    Set<QuizModel> quizes;

    public RoomModel() {
        this.startTime = LocalDateTime.now();
    }

    public void addQuiz(QuizModel quiz){
        quizes.add(quiz);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserModel> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserModel> participants) {
        this.participants = participants;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Set<QuizModel> getQuizes() {
        return quizes;
    }

    public void setQuizes(Set<QuizModel> quizes) {
        this.quizes = quizes;
    }
}
