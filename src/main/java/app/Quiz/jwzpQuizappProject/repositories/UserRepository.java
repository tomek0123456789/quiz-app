package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.controllers.UserController;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
}
