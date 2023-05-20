package app.Quiz.jwzpQuizappProject.controllers;


import app.Quiz.jwzpQuizappProject.models.CategoryModel;
import app.Quiz.jwzpQuizappProject.repositories.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin        // to allow frontend-backend connections
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping()
    public ResponseEntity getAllCategories() {
        return ResponseEntity.ok(this.categoryRepository.findAll());
    }

    // TODO: check if user is an admin
    @PostMapping()
    public ResponseEntity createCategory(@RequestBody CategoryModel newCategory) {

        if(!this.categoryRepository.findByName(newCategory.getName()).isEmpty()){
            return (ResponseEntity) ResponseEntity.badRequest();
        }else{
            this.categoryRepository.save(newCategory);
            return ResponseEntity.ok(newCategory);
        }

    }


}
