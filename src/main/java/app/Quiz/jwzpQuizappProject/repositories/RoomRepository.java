package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomModel, Long> {
}
