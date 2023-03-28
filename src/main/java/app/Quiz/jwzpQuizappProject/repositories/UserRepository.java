package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.Controllers.UserController;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserController, Long> {
}
