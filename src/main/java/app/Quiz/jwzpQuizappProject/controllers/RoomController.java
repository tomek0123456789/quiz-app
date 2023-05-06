package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.models.RoomModel;
import app.Quiz.jwzpQuizappProject.models.quizes.QuizModel;
import app.Quiz.jwzpQuizappProject.repositories.QuizRepository;
import app.Quiz.jwzpQuizappProject.repositories.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/myrooms")
public class RoomController {

    private  final RoomRepository roomRepository;
    private final QuizRepository quizRepository;    //is it correct to have 2 quizRespository?

    public RoomController(RoomRepository roomRepository, QuizRepository quizRepository) {
        this.roomRepository = roomRepository;
        this.quizRepository = quizRepository;
    }

    // TODO: zeby zwracalo tylko jesli uzytkownik autoryzowany
    @GetMapping("/{id}")
    public ResponseEntity getSingleRoom(@PathVariable long id) {
        return ResponseEntity.ok(this.roomRepository.findById(id));
    }

    // TODO: zeby zwracalo tylko jesli uzytkownik autoryzowany
    //      zwraca wszyskie pokoje ktorych user jest uczestnikiem lub wlascicielem,
    //      najlepiej je posortowac w ten spoosb, najpierw te ktore posiada, potem te w kt jest tylko uczestnikiem
    @GetMapping()
    public ResponseEntity getAllRooms() {
        return ResponseEntity.ok(this.roomRepository.findAll());    //temp
    }

    // TODO: Read user from token
    @PostMapping()
    public ResponseEntity createRoom(@RequestBody RoomModel newRoom) {
//        System.out.print(newRoom.getId());
        this.roomRepository.save(newRoom);
        return ResponseEntity.ok(newRoom);
    }

    @PostMapping("/{id}/quizes/{quizId}")
    public ResponseEntity addQuizToRoom(@PathVariable long id, @PathVariable long quizId) {
        Optional<RoomModel> room = this.roomRepository.findById(id);

        if (room.isPresent()){
            Optional<QuizModel> quiz = this.quizRepository.findById(quizId);
            quiz.ifPresent(quizModel -> room.get().addQuiz(quizModel));
            quiz.get().addRoom(room.get());
            this.roomRepository.save(room.get());
            this.quizRepository.save(quiz.get());

            return ResponseEntity.ok("ok");
        }

        return ResponseEntity.ok("coudnt add");
    }

    @DeleteMapping()
    public ResponseEntity deleteAllRooms() {
        this.roomRepository.deleteAll();
        return ResponseEntity.ok("ok");
    }

}
