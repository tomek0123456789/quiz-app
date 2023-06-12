package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends ListCrudRepository<UserModel, Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Optional<UserModel> findByEmail(String email);
    List<UserModel> findAllByNameContaining(String name);
    void deleteByEmail(String email);
}
