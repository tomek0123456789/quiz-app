package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizModel, Long> {
    List<QuizModel> findByTitle(String title);
    List<QuizModel> findAllByOwner(UserModel owner);
    List<QuizModel> findAllByTitleContaining(String titlePart);
    List<QuizModel> findAllByCategoryCategoryName(String categoryName);
    List<QuizModel> findAllByTitleContainingAndCategoryCategoryName(String titlePart, String categoryName);
}
