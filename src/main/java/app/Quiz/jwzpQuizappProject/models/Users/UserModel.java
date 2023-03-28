package app.Quiz.jwzpQuizappProject.models.Users;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class UserModel {
//    long id;
//    String name;
//    String email;
//    LocalDateTime createdAt;
//    Enum<UserStatus> status;
//    Enum<UserRole> role;
//    String passwordHash;
//    String salt;
//
//    UserModel(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    public UserModel() {
    }

    public UserModel(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
