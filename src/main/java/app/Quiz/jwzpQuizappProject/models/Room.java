package app.Quiz.jwzpQuizappProject.models;

import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Stack;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String name;

    @ManyToMany
    Stack<UserModel> participants;

    @ManyToOne
    UserModel owner;


    LocalDateTime startTime;

    @ManyToMany
    Stack<QuizModel> quizes;


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

    public Stack<UserModel> getParticipants() {
        return participants;
    }

    public void setParticipants(Stack<UserModel> participants) {
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

    public Stack<QuizModel> getQuizes() {
        return quizes;
    }

    public void setQuizes(Stack<QuizModel> quizes) {
        this.quizes = quizes;
    }
}
