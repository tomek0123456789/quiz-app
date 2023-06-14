package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.config.SecurityConfig;
import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.header.Header;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.mock.mockito.MockReset.apply;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(SecurityConfig.class)
//@WebAppConfiguration
//@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

//    @Autowired
//    private WebApplicationContext webApplicationContext;

    private String token = "SDfasdas";

//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .build();
//    }

//    @Test
////    @WithMockUser()
//    public void testGetMultipleCategories_WithoutNamePart_ShouldReturnAllCategories() throws Exception {
//        CategoryModel category1 = new CategoryModel();
//        category1.setId(1L);
//        category1.setName("Category 1");
//        CategoryModel category2 = new CategoryModel();
//        category1.setId(2L);
//        category1.setName("Category 2");
//        List<CategoryModel> categories = List.of(category1, category2);
//
//        when(categoryService.getAllCategories()).thenReturn(categories);
//
////        UserModel user = new UserModel();
//        mockMvc.perform(get("/categories"))
//                .andExpect(status().isOk());
////                .andExpect(jsonPath("$", hasSize(2)))
////                .andExpect(jsonPath("$[0].id", is(1)))
////                .andExpect(jsonPath("$[0].name", is("Category 1")))
////                .andExpect(jsonPath("$[1].id", is(2)))
////                .andExpect(jsonPath("$[1].name", is("Category 2")));
//
//        verify(categoryService).getAllCategories();
//    }

//    @Test
//    public void testGetMultipleCategories_WithNamePart_ShouldReturnFilteredCategories() throws Exception {
//        CategoryModel category1 = new CategoryModel(1L, "Category 1");
//        CategoryModel category2 = new CategoryModel(2L, "Category 2");
//        List<CategoryModel> categories = Arrays.asList(category1, category2);
//
//        when(categoryService.getCategoriesByNameContaining("cat")).thenReturn(categories);
//
//        mockMvc.perform(get("/categories").param("name", "cat"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].name", is("Category 1")))
//                .andExpect(jsonPath("$[1].id", is(2)))
//                .andExpect(jsonPath("$[1].name", is("Category 2")));
//
//        verify(categoryService).getCategoriesByNameContaining("cat");
//        verifyNoMoreInteractions(categoryService);
//    }
//
//    @Test
////    @WithMockUser(roles = "ADMIN")
//    public void testCreateCategory_ShouldReturnCreatedCategory() throws Exception {
//        CategoryDto categoryDto = new CategoryDto("New Category");
//        CategoryModel createdCategory = new CategoryModel(1L, "New Category");
//
//        when(categoryService.addCategory(categoryDto)).thenReturn(createdCategory);
//
//        mockMvc.perform(post("/categories")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\":\"New Category\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.name", is("New Category")));
//
//        verify(categoryService).addCategory(categoryDto);
//        verifyNoMoreInteractions(categoryService);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testUpdateCategory_ShouldReturnUpdatedCategory() throws Exception {
//        CategoryDto categoryDto = new CategoryDto("Updated Category");
//        CategoryModel updatedCategory = new CategoryModel(1L, "Updated Category");
//
//        when(categoryService.updateCategory(categoryDto, 1L)).thenReturn(updatedCategory);
//
//        mockMvc.perform(put("/categories/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\":\"Updated Category\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.name", is("Updated Category")));
//
//        verify(categoryService).updateCategory(categoryDto, 1L);
//        verifyNoMoreInteractions(categoryService);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testDeleteCategory_ShouldReturnNoContent() throws Exception {
//        mockMvc.perform(delete("/categories/1"))
//                .andExpect(status().isNoContent());
//
//        verify(categoryService).deleteCategory(1L);
//        verifyNoMoreInteractions(categoryService);
//    }

}
