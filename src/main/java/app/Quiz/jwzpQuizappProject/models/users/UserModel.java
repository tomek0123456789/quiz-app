package app.Quiz.jwzpQuizappProject.models.users;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Set;

// TODO too much info is being returned when quiz is being added
// consider JsonIgnore there (QuizModel.owner) or here
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotBlank
    @Column(unique = true)
    private String name;
    @NotBlank
    @Email
    @Column(unique = true)
    String email;
    @NonNull
    Instant createdAt;
    @NonNull
    UserStatus status;
    @NonNull
    List<UserRole> roles;
    @NonNull
    @JsonIgnore
    String password;

    // do jakich pokoi nalzy user
    @ManyToMany
    @JsonIgnore
    Set<RoomModel> roomParticipation;

//    @OneToMany(mappedBy = "owner")
//    Set<QuizModel> quizes;

//todo add constructor to add an admin, also add time parameter (no injection to model?)
    public UserModel(@NotBlank String name, @NotBlank String email, @NonNull String password, Instant createdAt) {
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.status = UserStatus.ACTIVE;
        this.roles = List.of(UserRole.USER);
        this.password = password;
    }

    public UserModel(Long id, @NonNull String name, @NonNull String email,
                     @NonNull UserStatus status, @NonNull List<UserRole> roles,
                     @NonNull String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = Instant.now();
        this.status = status;
        this.roles = roles;
        this.password = password;
    }

    public UserModel() {
        this.name = "admin";
        this.email = "admin@admin.com";
        this.createdAt = Instant.now();
        this.status = UserStatus.ACTIVE;
        this.roles = List.of(UserRole.USER, UserRole.ADMIN);
        this.password = new BCryptPasswordEncoder().encode("admin");;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
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

    public Set<RoomModel> getRoomParticipation() {
        return roomParticipation;
    }

    public void setRoomParticipation(Set<RoomModel> roomParticipation) {
        this.roomParticipation = roomParticipation;
    }
    public void addRoomParticipation(RoomModel room) {
        roomParticipation.add(room);
    }
    public void removeRoomParticipation(RoomModel room) {
        roomParticipation.remove(room);
    }
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
                '}';
    }
}
