package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryDto;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<CategoryModel> getSingleCategory(long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public List<CategoryModel> getAllCategories() {
        return categoryRepository.findAll();
    }
    public List<CategoryModel> getCategoriesByNameContaining(String namePart) {
        return categoryRepository.findAllByCategoryNameContaining(namePart);
    }

    public CategoryModel addCategory(CategoryDto categoryDto) throws CategoryAlreadyExistsException {
        if (categoryRepository.findByCategoryName(categoryDto.categoryName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name: " + categoryDto.categoryName() + " already exists.");
        }
        return categoryRepository.save(new CategoryModel(categoryDto.categoryName()));
    }

    public CategoryModel updateCategory(CategoryDto categoryDto, long categoryId) {
        var category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            var updatedCategory = category.get();
            updatedCategory.setName(categoryDto.categoryName());
            return categoryRepository.save(updatedCategory);
        }
        return categoryRepository.save(new CategoryModel(categoryDto.categoryName()));
    }

    public void deleteCategory(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
