package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.results.QuestionAndUsersAnswerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAndUsersAnswerRepository extends JpaRepository<QuestionAndUsersAnswerModel, Long> {
}

