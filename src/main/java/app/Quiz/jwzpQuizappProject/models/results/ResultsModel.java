package app.Quiz.jwzpQuizappProject.models.results;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class ResultsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToMany
    Set<QuizResultsModel> quizesResults;

    public ResultsModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<QuizResultsModel> getQuizesResults() {
        return quizesResults;
    }

    public void setQuizesResults(Set<QuizResultsModel> quizesResults) {
        this.quizesResults = quizesResults;
    }
}
