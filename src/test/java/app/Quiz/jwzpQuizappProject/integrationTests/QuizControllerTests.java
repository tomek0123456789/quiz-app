package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.controllers.QuizController;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerDto;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionDto;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizPatchDto;
import app.Quiz.jwzpQuizappProject.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(QuizController.class)
public class QuizControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    @WithMockUser()
    public void testGetSingleQuiz_ShouldReturnQuiz() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        when(quizService.getSingleQuiz(1L)).thenReturn(quiz);

        mockMvc.perform(get("/quizzes/1"));

        verify(quizService).getSingleQuiz(1L);
    }

    @Test
    @WithMockUser()
    public void testGetMultipleQuizzes_ShouldReturnAllQuizzes() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        when(quizService.getMultipleQuizzes(Optional.empty(),Optional.empty(),Optional.empty())).thenReturn(new ArrayList<QuizModel>());

        mockMvc.perform(get("/quizzes")).andExpect(status().isOk());

        verify(quizService).getMultipleQuizzes(Optional.empty(),Optional.empty(),Optional.empty());
    }

    @Test
    @WithMockUser()
    public void testGetMultipleQuizzes_WithNameParam_ShouldReturnQuizzes() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        Optional<String> name = Optional.of("a");
        Optional<String> categoryName = Optional.empty();
        Optional<Boolean> valid = Optional.empty();

        when(quizService.getMultipleQuizzes(name, categoryName, valid)).thenReturn(new ArrayList<QuizModel>());

        mockMvc.perform(get("/quizzes?name=a")).andExpect(status().isOk());

        verify(quizService).getMultipleQuizzes(name, categoryName, valid);
    }

    @Test
    @WithMockUser()
    public void testGetMultipleQuizzes_WithNameAndCategoryNameParam_ShouldReturnQuizzes() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        Optional<String> name = Optional.of("a");
        Optional<String> categoryName = Optional.of("b");
        Optional<Boolean> valid = Optional.empty();

        when(quizService.getMultipleQuizzes(name, categoryName, valid)).thenReturn(new ArrayList<QuizModel>());

        mockMvc.perform(get("/quizzes?name=a&category=b")).andExpect(status().isOk());

        verify(quizService).getMultipleQuizzes(name, categoryName, valid);
    }


    @Test
    @WithMockUser()
    public void testGetMultipleQuizzes_WithNameAndCategoryNameAndValidParam_ShouldReturnQuizzes() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        Optional<String> name = Optional.of("a");
        Optional<String> categoryName = Optional.of("b");
        Optional<Boolean> valid = Optional.of(true);

        when(quizService.getMultipleQuizzes(name, categoryName, valid)).thenReturn(new ArrayList<QuizModel>());

        mockMvc.perform(get("/quizzes?name=a&category=b&valid=true")).andExpect(status().isOk());

        verify(quizService).getMultipleQuizzes(name, categoryName, valid);
    }

    @Test
    @WithMockUser()
    public void testGetMultipleQuizzes_CategoryNameAndValidParams_ShouldReturnQuizzes() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        Optional<String> name = Optional.empty();
        Optional<String> categoryName = Optional.of("b");
        Optional<Boolean> valid = Optional.of(true);

        when(quizService.getMultipleQuizzes(name, categoryName, valid)).thenReturn(new ArrayList<QuizModel>());

        mockMvc.perform(get("/quizzes?category=b&valid=true")).andExpect(status().isOk());
        verify(quizService).getMultipleQuizzes(name, categoryName, valid);
    }

    @Test
    @WithMockUser
    public void testGetMyQuizzes_ShouldReturnUserQuizzes() throws Exception {
        String token = "test-token";

        QuizModel quiz1 = new QuizModel();
        quiz1.setId(1L);
        quiz1.setTitle("Quiz 1");

        QuizModel quiz2 = new QuizModel();
        quiz2.setId(2L);
        quiz2.setTitle("Quiz 2");

        List<QuizModel> quizzes = List.of(quiz1, quiz2);

        when(quizService.getUserQuizzes(token)).thenReturn(quizzes);

        mockMvc.perform(get("/quizzes/my")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON));

        verify(quizService).getUserQuizzes(token);
    }

    @Test
    @WithMockUser
    public void testCreateQuiz_ShouldReturnCreatedQuiz() throws Exception {
        String token = "test-token";

        QuizDto quizDto = new QuizDto("name", "desc", 1);

        QuizModel createdQuiz = new QuizModel();
        createdQuiz.setId(1L);
        createdQuiz.setTitle("New Quiz");

        when(quizService.addQuiz(quizDto, token)).thenReturn(createdQuiz);

        mockMvc.perform(post("/quizzes")
                        .with(csrf()) // Dodanie CSRF Tokena do żądania
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quizDto)));

        verify(quizService).addQuiz(quizDto, token);
    }

    private static String asJsonString(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
