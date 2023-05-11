package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultsRepository extends JpaRepository<ResultsModel, Long> {
}
