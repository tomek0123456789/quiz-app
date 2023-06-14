package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @WithMockUser()
    public void testGetMultipleCategories_WithoutNamePart_ShouldReturnAllCategories() throws Exception {
        CategoryModel category1 = new CategoryModel();
        category1.setId(1L);
        category1.setName("Category 1");
        CategoryModel category2 = new CategoryModel();
        category2.setId(2L);
        category2.setName("Category 2");
        List<CategoryModel> categories = List.of(category1, category2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @WithMockUser()
    public void testGetMultipleCategories_WithNamePart_ShouldReturnAllCategories() throws Exception {
        String namePart = "One";

        CategoryModel category1 = new CategoryModel();
        category1.setId(1L);
        category1.setName("CategoryOne");
        CategoryModel category2 = new CategoryModel();
        category2.setId(2L);
        category2.setName("CategoryOne");
        List<CategoryModel> categories = List.of(category1, category2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories?name=" + namePart).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).getCategoriesByNameContaining(namePart);
    }


}
