package app.Quiz.jwzpQuizappProject.models.categories;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class CategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @Column(unique = true)
    String name;

    public CategoryModel(String categoryName) {
        this.name = categoryName;
    }

    protected CategoryModel() {}

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
    @Override
    public String toString() {
        return "CategoryModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}