package app.Quiz.jwzpQuizappProject.models.users;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


// TODO add validation to match the @Valid annotation
// TODO how to add salt etc.

// TODO too much info is being returned when quiz is being added
// consider JsonIgnore there (QuizModel.owner) or here
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    todo change the strategy or add a generator
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String name;
    @NotBlank
    @Email
//  does this need @Email validation when RegisterRequest has it already?
    @Column(unique = true)
    String email;
    @NonNull
    LocalDateTime createdAt;
    @NonNull
    UserStatus status;
    @NonNull
    List<UserRole> roles;
    @NonNull
    @JsonIgnore
    String password;
    @NonNull
    @JsonIgnore
    String salt;

    // do jakich pokoi nalzy user
    @ManyToMany
    @JsonIgnore
    Set<RoomModel> quizzesParticipation;

//    @OneToMany(mappedBy = "owner")
//    Set<QuizModel> quizes;

//todo add constructor to add an admin, also add time parameter (no injection to model?)
    public UserModel(@NotBlank String name, @NotBlank String email, @NonNull String password) {
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.roles = List.of(UserRole.USER);
        this.password = password;
        this.salt = "123salt!!";
    }

    public UserModel(Long id, @NonNull String name, @NonNull String email,
                     @NonNull UserStatus status, @NonNull List<UserRole> roles,
                     @NonNull String password, @NonNull String salt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.status = status;
        this.roles = roles;
        this.password = password;
        this.salt = salt;
    }

    public UserModel() {
        this.name = "admin";
        this.email = "admin@admin.com";
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.roles = List.of(UserRole.USER, UserRole.ADMIN);
        this.password = new BCryptPasswordEncoder().encode("admin");;
        this.salt = "123salt!!";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Enum<UserStatus> getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Set<RoomModel> getQuizzesParticipation() {
        return quizzesParticipation;
    }

    public void setQuizzesParticipation(Set<RoomModel> quizesParticipation) {
        this.quizzesParticipation = quizesParticipation;
    }
    public void addRoomParticipation(RoomModel room) {
        quizzesParticipation.add(room);
    }
    public void removeRoomParticipation(RoomModel room) {
        quizzesParticipation.remove(room);
    }

//    public Set<QuizModel> getQuizes() {
//        return quizes;
//    }
//
//    public void setQuizes(Set<QuizModel> quizes) {
//        this.quizes = quizes;
//    }
//
//    public void addQuiz(QuizModel quiz) {
//        this.quizes.add(quiz);
//    }

    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN);
    }
    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", role=" + roles +
                ", passwordHash='" + password + '\'' +
                ", salt='" + salt + '\'' +
//                ", quizesParticipation=" + quizesParticipation +
                '}';
    }
}
