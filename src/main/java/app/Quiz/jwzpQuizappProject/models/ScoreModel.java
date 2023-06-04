package app.Quiz.jwzpQuizappProject.models;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;


// TODO: add more fields, like score owner, which quiz etc etc
// add repo to it
@Entity
public class ScoreModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    long value;

    public ScoreModel(long value) {
        this.value = value;
    }

    public ScoreModel() {
        this.value = 0;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
