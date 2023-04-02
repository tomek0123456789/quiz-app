package app.Quiz.jwzpQuizappProject.models.users;


import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    String email;
    @NonNull
    LocalDateTime createdAt;
    @NonNull
    Enum<UserStatus> status;

    @NonNull
    Enum<UserRole> role;

    @NonNull
    String passwordHash;

    @NonNull
    String salt;

//    @OneToMany(mappedBy = "owner")
//    Set<QuizModel> quizes;


    public UserModel(@NonNull String name,@NonNull String email) {
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.role = UserRole.USER;
        this.passwordHash = "password123";
        this.salt = "123salt!!";
    }

    public UserModel(Long id, @NonNull String name, @NonNull String email,
                     @NonNull Enum<UserStatus> status, @NonNull Enum<UserRole> role,
                     @NonNull String passwordHash, @NonNull String salt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.status = status;
        this.role = role;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public UserModel() {
        this.name = "example_name";
        this.email = "example@email";
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.role = UserRole.USER;
        this.passwordHash = "password123";
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

    public void setStatus(Enum<UserStatus> status) {
        this.status = status;
    }

    public Enum<UserRole> getRole() {
        return role;
    }

    public void setRole(Enum<UserRole> role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
}
