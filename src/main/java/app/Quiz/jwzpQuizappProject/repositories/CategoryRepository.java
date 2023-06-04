package app.Quiz.jwzpQuizappProject.repositories;

import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<CategoryModel, Long> {

    List<CategoryModel> findAllByNameContaining(String name);
    Optional<CategoryModel> findByName(String name);
    void deleteById(long id);
}

