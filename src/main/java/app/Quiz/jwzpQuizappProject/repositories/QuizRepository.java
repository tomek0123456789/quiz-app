package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizStatus;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizModel, Long> {
    List<QuizModel> findAllByOwner(UserModel owner);
    List<QuizModel> findAllByTitleContaining(String titlePart);
    List<QuizModel> findAllByCategoryName(String categoryName);
    List<QuizModel> findAllByQuizStatus(QuizStatus quizStatus);
    List<QuizModel> findAllByTitleContainingAndCategoryName(String titlePart, String categoryName);
    List<QuizModel> findAllByTitleContainingAndQuizStatus(String titlePart, QuizStatus quizStatus);
    List<QuizModel> findAllByCategoryNameAndQuizStatus(String categoryName, QuizStatus quizStatus);
    List<QuizModel> findAllByTitleContainingAndCategoryNameAndQuizStatus(String titlePart, String categoryName, QuizStatus quizStatus);
}
