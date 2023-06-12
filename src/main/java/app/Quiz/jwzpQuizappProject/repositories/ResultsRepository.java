package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ResultsRepository extends JpaRepository<ResultsModel, Long> {
    List<ResultsModel> findAllByOwner(UserModel owner);
    List<ResultsModel> findByRoomId(long roomId);
    @Query("select res from ResultsModel res join res.quizzesResults q where q.id = :quiz_results_id and res.owner.id = :owner_id")
    Set<QuizResultsModel> findAllByOwnerAndQuizzesResultsId(@Param("owner_id") long ownerId, @Param("quiz_results_id") long quizResultsId);
}
