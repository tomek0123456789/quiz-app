package app.Quiz.jwzpQuizappProject.models.quizzes;

import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionStatus;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "quizzes")
public class QuizModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String description;
    private QuizStatus quizStatus;
    // TODO: make it all to lazy (optional = false,fetch=FetchType.LAZY)
    @ManyToOne()
    @JsonIgnore
    private UserModel owner;
    @OneToMany(mappedBy = "quizId")
    @OrderBy("ordNum ASC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<QuestionModel> questions;
    @NonNull
    Instant createdAt;
    // @OneToMany(mappedBy = "id")
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
    @JsonIgnore
    int validQuestions;

    public QuizModel(@NonNull String title, @NonNull String description, UserModel owner, CategoryModel category, Instant createdAt) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.createdAt = createdAt;
        this.category = category;
        this.quizStatus = QuizStatus.INVALID;
        this.validQuestions = 0;
        this.questions = Collections.emptyList();
        this.rooms = Collections.emptySet();
    }

<<<<<<< HEAD
    public QuizModel() {
    }
=======
    protected QuizModel() {}
>>>>>>> main

    public void addRoom(RoomModel room){
        rooms.add(room);
    }
    public void removeRoom(RoomModel room){
        rooms.remove(room);
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @NonNull
    public String getTitle() {
        return title;
    }
    public void setTitle(@NonNull String name) {
        this.title = name;
    }
    @NonNull
    public String getDescription() {
        return description;
    }
    public void setDescription(@NonNull String description) {
        this.description = description;
    }
    public long getOwnerId() {
        return owner.getId();
    }
    public void setOwner(UserModel owner) {
        this.owner = owner;
    }
    @NonNull
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(@NonNull Instant createdAt) {
        this.createdAt = createdAt;
    }
    public UserModel getOwner() {
        return owner;
    }
    public List<QuestionModel> getQuestions() {
        return questions;
    }
    public int questionsSize() {
        return questions.size();
    }
    public int nextQuestionOrdinalNumber() {
        return questions.size() + 1;
    }
    public void setQuestions(List<QuestionModel> questions) {
        this.questions = Collections.emptyList();
        questions.forEach(this::addQuestion);
    }
    public void addQuestion(QuestionModel question) {
        questions.add(question);
        if (question.getQuestionStatus() == QuestionStatus.VALID) {
            validQuestions++;
        }
        if (validQuestions >= 2) {
            quizStatus = QuizStatus.VALID;
        }
    }
    public QuestionModel getSingleQuestionByOrdNum(int questionOrdNum) throws QuestionNotFoundException {
        return questions.stream()
                .filter(q -> q.getOrdNum() == questionOrdNum)
                .findFirst()
                .orElseThrow(QuestionNotFoundException::new);
    }
    public QuestionModel removeQuestion(int questionOrdNum) throws QuestionNotFoundException {
        var question = getSingleQuestionByOrdNum(questionOrdNum);
        questions.remove(question);
        setQuestionOrderNumbers();
        return question;
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
    public QuizStatus getQuizStatus() {
        return quizStatus;
    }
    public void setQuizStatus(QuizStatus quizStatus) {
        this.quizStatus = quizStatus;
    }
    private void setQuestionOrderNumbers() {
        for (int i = 0; i < questionsSize(); i++) {
            questions.get(i).setOrdNum(i + 1);
        }
    }

    @Override
    public String toString() {
        return "QuizModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", questions=" + questions +
                ", createdAt=" + createdAt +
                ", categoryModel=" + category.getName() +
                ", category=" + category +
                ", rooms=" + rooms +
                '}';
    }
}
