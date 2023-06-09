package app.Quiz.jwzpQuizappProject.models.categories;

import jakarta.persistence.*;

@Entity
@Table
public class CategoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Column(unique = true)
    String name;

    public CategoryModel(String name) {
        this.name = name;
    }

    protected CategoryModel() {
// ASK todo should an exception be thrown here? or how can i restrict this constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String categoryName) {
        this.name = categoryName;
    }
}
