package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryDto;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin        // to allow frontend-backend connections
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryModel> getMultipleCategories(
            @RequestParam(value = "name", required = false) Optional<String> namePart
    ) {
        if (namePart.isPresent()) {
            return categoryService.getCategoriesByNameContaining(namePart.get());
        } else {
            return categoryService.getAllCategories();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryModel> createCategory(@RequestBody CategoryDto categoryDto) throws CategoryAlreadyExistsException {
        return new ResponseEntity<>(categoryService.addCategory(categoryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoryModel updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable long categoryId) {
        return categoryService.updateCategory(categoryDto, categoryId);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
