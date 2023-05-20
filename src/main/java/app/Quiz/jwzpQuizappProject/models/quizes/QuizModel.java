package app.Quiz.jwzpQuizappProject.models.quizes;

import app.Quiz.jwzpQuizappProject.models.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.RoomModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jdk.jfr.Category;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Stack;

@Entity
public class QuizModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    // TODO: make it all to lazy (optional = false,fetch=FetchType.LAZY)
    @ManyToOne()
    private UserModel owner;

    @OneToMany
    @OrderBy("ordNum ASC")
    private List<QuestionModel> questions;

    @NonNull
    LocalDateTime createdAt;

    // @OneToMany(mappedBy = "id")
    long categoryId;

    @ManyToOne
    @JoinColumn(name = "category")
    CategoryModel category;

    // do jakich pokoi nalezy quiz
    @ManyToMany(fetch = FetchType.LAZY,  cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JsonIgnore //to prevent infinite recursion
    Set<RoomModel> rooms;


    public QuizModel(@NonNull String name, @NonNull String description, UserModel owner, long categoryId) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
        this.categoryId = categoryId;
    }

    public QuizModel() {
        this.name = "No name provided ";
        this.description = "no description provided";
        this.createdAt = LocalDateTime.now();
        this.categoryId = 0;
    }

    public void addRoom(RoomModel room){
        rooms.add(room);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public UserModel getOwnerId() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    @NonNull
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public UserModel getOwner() {
        return owner;
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionModel> questions) {
        this.questions = questions;
    }

    public void addQuestion(QuestionModel question) {
        this.questions.add(question);
        question.setOrdNum(this.questions.size() - 1);
    }

    public void addQuestionAt(QuestionModel question, Integer index) {
        this.questions.add(index,question);

        System.out.println(index);

        for(int i = index; i < this.questions.size(); i++){
            this.questions.get(i).setOrdNum(i);
        }
    }

    public QuestionModel getQuestionByOrdNum(Integer ordNum) {
        if(ordNum >= this.questions.size()){
            return null;
        }
        return this.questions.get(ordNum);
    }

    public void deleteQuestion(QuestionModel question) {
        this.questions.remove(question);

        for(int i = 0; i < this.questions.size(); i++){
            this.questions.get(i).setOrdNum(i);
        }

    }

    public Set<RoomModel> getRooms() {
        return rooms;
    }

    public void setRooms(Set<RoomModel> rooms) {
        this.rooms = rooms;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public CategoryModel getCategory() {
        return category;
    }
}
