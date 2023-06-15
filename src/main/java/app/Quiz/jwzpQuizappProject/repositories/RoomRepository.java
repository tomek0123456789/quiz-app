package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomModel, Long> {
    List<RoomModel> findAllByOwner(UserModel user);
    List<RoomModel> findAllByOwnerOrParticipantsContaining(UserModel user1, UserModel user2);
}
