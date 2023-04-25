package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.models.RoomModel;
import app.Quiz.jwzpQuizappProject.repositories.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/myrooms")
public class RoomController {

    private  final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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
        System.out.print(newRoom.getId());
        this.roomRepository.save(newRoom);
        return ResponseEntity.ok(newRoom.getId());
    }

    @DeleteMapping()
    public ResponseEntity deleteAllRooms() {
        this.roomRepository.deleteAll();
        return ResponseEntity.ok("ok");
    }

}
