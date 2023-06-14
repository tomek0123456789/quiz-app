package app.Quiz.jwzpQuizappProject.controllers;

import app.Quiz.jwzpQuizappProject.config.Constants;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryDto;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
    private final Logger log = LoggerFactory.getLogger(Constants.LOGGER_NAME);
    private final CategoryService categoryService;
    private final TokenService tokenService;

    public CategoryController(CategoryService categoryService, TokenService tokenService) {
        this.categoryService = categoryService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public List<CategoryModel> getMultipleCategories(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(value = "name", required = false) Optional<String> namePart
    ) {
        String userEmail = tokenService.getEmailFromToken(token);
        if (namePart.isPresent()) {
            log.info("User with email: " + userEmail + " gets categories with name part: " + namePart.get() + ".");
            return categoryService.getCategoriesByNameContaining(namePart.get());
        }
        log.info("User with email: " + userEmail + " gets all categories.");
        return categoryService.getAllCategories();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryModel> createCategory(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody CategoryDto categoryDto
    ) throws CategoryAlreadyExistsException {
        String userEmail = tokenService.getEmailFromToken(token);
        log.info("User with email: " + userEmail + " tries to create category.");
        var category = categoryService.addCategory(categoryDto);
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " created category: " + category + ".");
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoryModel updateCategory(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody CategoryDto categoryDto,
            @PathVariable long categoryId
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " wants to update category with id: " + categoryId + ".");
        var category = categoryService.updateCategory(categoryDto, categoryId);
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " updated category with id: " + categoryId + " to: " + category + ".");
        return category;
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCategory(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable long categoryId
    ) {
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " wants to delete category with id: " + categoryId + ".");
        categoryService.deleteCategory(categoryId);
        log.info("User with email: " + tokenService.getEmailFromToken(token) + " deleted category with id: " + categoryId + ".");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
