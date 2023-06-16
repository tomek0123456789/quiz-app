package app.Quiz.jwzpQuizappProject.models.rooms;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
//import org.hibernate.internal.util.collections.Stack;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
public class RoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    String roomName;
    @ManyToMany
    Set<UserModel> participants;
    @ManyToOne
    UserModel owner;
    Instant startTime;
    Instant endTime;

    @ManyToMany(fetch = FetchType.LAZY,  cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    Set<QuizModel> quizzes;

    public RoomModel() {
        this.participants = new HashSet<>();
    }

    public RoomModel(String roomName, UserModel owner, Instant startTime, Instant endTime) {
        this.roomName = roomName;
        this.owner = owner;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = Collections.emptySet();
        this.quizzes = Collections.emptySet();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String name) {
        this.roomName = name;
    }

    public Set<UserModel> getParticipants() {
        return participants;
    }
    public void addParticipant(UserModel participant) {
        participants.add(participant);
    }
    public void removeParticipant(UserModel user) {
        this.participants.remove(user);
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
    public long getOwnerId() {
        return owner.getId();
    }
    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Set<QuizModel> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<QuizModel> quizes) {
        this.quizzes = quizes;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    // uzywam tego na froncie!!
    public long getMaxScore(){
        long maxscore = 0;
        for(var quiz : quizzes){
            maxscore += quiz.getQuestions().size();
        }

        return maxscore;
    }
    public boolean isPastFinishTime(Instant currentTime) {
        return currentTime.isAfter(endTime);
    }
    public void addQuiz(QuizModel quiz){
        quizzes.add(quiz);
    }
    public void removeQuiz(QuizModel quiz) {
        quizzes.remove(quiz);
    }

    public void updateWithPutDto(RoomPutDto roomPutDto) {
        this.roomName = roomPutDto.roomName();
        this.startTime = roomPutDto.startTime();
        this.endTime = roomPutDto.endTime();
        this.owner = roomPutDto.owner();
    }

    public void updateWithPatchDto(RoomPatchDto roomPatchDto) {
        this.roomName = roomPatchDto.roomName() != null ? roomPatchDto.roomName() : this.roomName;
        this.startTime = roomPatchDto.startTime()!= null ? roomPatchDto.startTime() : this.startTime;
        this.endTime = roomPatchDto.endTime()!= null ? roomPatchDto.endTime() : this.endTime;
    }

    @Override
    public String toString() {
        return "RoomModel{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", participants=" + participants +
                ", owner=" + owner +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
