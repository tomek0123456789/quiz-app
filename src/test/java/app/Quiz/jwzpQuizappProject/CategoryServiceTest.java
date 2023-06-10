package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryAlreadyExistsException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryNotFoundException;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryDto;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.repositories.CategoryRepository;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest()
public class CategoryServiceTest {

    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    public void testGetSingleCategory_ExistingCategory_ReturnsCategory() throws CategoryNotFoundException {
        long categoryId = 1;
        CategoryModel expectedCategory = new CategoryModel("TestCategory");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        CategoryModel result = categoryService.getSingleCategory(categoryId);

        assertEquals(expectedCategory, result);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void testGetSingleCategory_NonExistingCategory_ThrowsCategoryNotFoundException() {
        long categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getSingleCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void testGetAllCategories_ReturnsAllCategories() {
        List<CategoryModel> expectedCategories = new ArrayList<>();
        expectedCategories.add(new CategoryModel("TestCategory1"));
        expectedCategories.add(new CategoryModel("TestCategory2"));
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        List<CategoryModel> result = categoryService.getAllCategories();

        assertEquals(expectedCategories, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testGetCategoriesByNameContaining_ReturnsCategoriesWithNameContaining() {
        String namePart = "Test";
        List<CategoryModel> expectedCategories = new ArrayList<>();
        expectedCategories.add(new CategoryModel("TestCategory1"));
        expectedCategories.add(new CategoryModel("TestCategory2"));
        when(categoryRepository.findAllByNameContaining(namePart)).thenReturn(expectedCategories);

        List<CategoryModel> result = categoryService.getCategoriesByNameContaining(namePart);

        assertEquals(expectedCategories, result);
        verify(categoryRepository, times(1)).findAllByNameContaining(namePart);
    }

    @Test
    public void testAddCategory_ValidCategoryDto_ReturnsAddedCategory() throws CategoryAlreadyExistsException {
        CategoryDto categoryDto = new CategoryDto("TestCategory");
        when(categoryRepository.findByName(categoryDto.categoryName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(CategoryModel.class))).thenReturn(new CategoryModel(categoryDto.categoryName()));

        CategoryModel result = categoryService.addCategory(categoryDto);

        assertEquals(categoryDto.categoryName(), result.getName());
        verify(categoryRepository, times(1)).findByName(categoryDto.categoryName());
        verify(categoryRepository, times(1)).save(any(CategoryModel.class));
    }

    @Test
    public void testAddCategory_DuplicateCategoryDto_ThrowsCategoryAlreadyExistsException() {
        CategoryDto categoryDto = new CategoryDto("TestCategory");
        when(categoryRepository.findByName(categoryDto.categoryName())).thenReturn(Optional.of(new CategoryModel(categoryDto.categoryName())));

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.addCategory(categoryDto));
        verify(categoryRepository, times(1)).findByName(categoryDto.categoryName());
        verify(categoryRepository, never()).save(any(CategoryModel.class));
    }

    @Test
    public void testUpdateCategory_ExistingCategory_ReturnsUpdatedCategory() {
        long categoryId = 1;
        CategoryDto categoryDto = new CategoryDto("TestCategoryUpdated");
        CategoryModel existingCategory = new CategoryModel("TestCategory");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(CategoryModel.class))).thenReturn(existingCategory);

        CategoryModel result = categoryService.updateCategory(categoryDto, categoryId);

        assertEquals(categoryDto.categoryName(), result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(CategoryModel.class));
    }

    @Test
    public void testUpdateCategory_NonExistingCategory_ReturnsAddedCategory() {
        long categoryId = 1;
        CategoryDto categoryDto = new CategoryDto("TestCategoryUpdated");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(CategoryModel.class))).thenReturn(new CategoryModel(categoryDto.categoryName()));

        CategoryModel result = categoryService.updateCategory(categoryDto, categoryId);

        assertEquals(categoryDto.categoryName(), result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(CategoryModel.class));
    }

    @Test
    public void testDeleteCategory_ValidCategoryId_VerifyDeletion() {
        long categoryId = 1;

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
