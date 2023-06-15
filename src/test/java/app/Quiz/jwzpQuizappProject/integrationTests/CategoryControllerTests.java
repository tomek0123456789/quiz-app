package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryDto;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static app.Quiz.jwzpQuizappProject.integrationTests.IntTestsHelper.asJsonString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(CategoryController.class)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser
    public void testGetMultipleCategories_WithoutNamePart_ShouldReturnAllCategories() throws Exception {
        long cat1Id = 1;
        long cat2Id = 2;
        String cat1Name = "Category 1";
        String cat2Name = "Category 2";

        CategoryModel category1 = new CategoryModel();
        category1.setId(cat1Id);
        category1.setName(cat1Name);
        CategoryModel category2 = new CategoryModel();
        category2.setId(cat2Id);
        category2.setName(cat2Name);

        List<CategoryModel> categories = List.of(category1, category2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(cat1Id))
                .andExpect(jsonPath("$[0].name").value(cat1Name))
                .andExpect(jsonPath("$[1].id").value(cat2Id))
                .andExpect(jsonPath("$[1].name").value(cat2Name));

        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser
    public void testGetMultipleCategories_WithNamePart_ShouldReturnMatchingCategories() throws Exception {
        long cat1Id = 1;
        long cat2Id = 2;
        String cat1Name = "Category 1";
        String cat2Name = "Category 2";

        CategoryModel category1 = new CategoryModel();
        category1.setId(cat1Id);
        category1.setName(cat1Name);
        CategoryModel category2 = new CategoryModel();
        category2.setId(cat2Id);
        category2.setName(cat2Name);
        List<CategoryModel> categories = List.of(category1, category2);

        when(categoryService.getCategoriesByNameContaining("cat")).thenReturn(categories);

        mockMvc.perform(get("/categories")
                        .param("name", "cat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(cat1Id))
                .andExpect(jsonPath("$[0].name").value(cat1Name))
                .andExpect(jsonPath("$[1].id").value(cat2Id))
                .andExpect(jsonPath("$[1].name").value(cat2Name));

        verify(categoryService).getCategoriesByNameContaining("cat");
    }

    @Test
    @WithMockUser(username = "regularUser", authorities = {"USER" })
    public void testCreateCategory_NoAdmin_ShouldReturn403() throws Exception {
        CategoryDto categoryDto = new CategoryDto("New Category");

        CategoryModel createdCategory = new CategoryModel();
        createdCategory.setId(1L);
        createdCategory.setName("New Category");

        when(categoryService.addCategory(categoryDto)).thenReturn(createdCategory);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(categoryDto)))
                .andExpect(status().isForbidden());

        verify(categoryService, times(0)).addCategory(categoryDto);
    }

    @Test
    @WithMockUser(username = "regularUser", authorities = {"USER" })
    public void testUpdateCategory_ShouldReturnUpdatedCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto("Updated Category");

        CategoryModel updatedCategory = new CategoryModel();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Category");

        when(categoryService.updateCategory(categoryDto, 1L)).thenReturn(updatedCategory);

        mockMvc.perform(put("/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(categoryDto)))
                .andExpect(status().isForbidden());

        verify(categoryService, times(0)).updateCategory(categoryDto, 1L);
    }
}
