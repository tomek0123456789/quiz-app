package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.controllers.QuizController;
import app.Quiz.jwzpQuizappProject.exceptions.users.UserNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerDto;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionDto;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizPatchDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserRole;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import app.Quiz.jwzpQuizappProject.service.QuizService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static app.Quiz.jwzpQuizappProject.integrationTests.IntTestsHelper.asJsonString;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(QuizController.class)
public class QuizControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    private String token = "Baerer token";

    @Test
    @WithMockUser()
    public void testGetSingleQuiz_ShouldReturnQuiz() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        when(quizService.getSingleQuiz(1L)).thenReturn(quiz);

        mockMvc.perform(get("/quizzes/1").with(csrf())
                .header(HttpHeaders.AUTHORIZATION, token));

        verify(quizService).getSingleQuiz(1L);
    }

    @Test
    @WithMockUser()
    public void testGetMultipleQuizzes_ShouldReturnAllQuizzes() throws Exception {
        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setTitle("Quiz 1");

        when(quizService.getMultipleQuizzes(Optional.empty(),Optional.empty(),Optional.empty())).thenReturn(new ArrayList<QuizModel>());

        mockMvc.perform(get("/quizzes").with(csrf())
                .header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isOk());

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

        mockMvc.perform(get("/quizzes?name=a").with(csrf())
                .header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isOk());

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

        mockMvc.perform(get("/quizzes?name=a&category=b").with(csrf())
                .header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isOk());

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

        mockMvc.perform(get("/quizzes?name=a&category=b&onlyValidQuizzes=true").with(csrf())
                .header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isOk());

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

        mockMvc.perform(get("/quizzes?category=b&onlyValidQuizzes=true").with(csrf())
                .header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isOk());
        verify(quizService).getMultipleQuizzes(name, categoryName, valid);
    }

    @Test
    @WithMockUser
    public void testGetMyQuizzes_ValidToken_ShouldReturnUserQuizzes() throws Exception {
        // Mock data
        QuizModel quiz1 = new QuizModel();
        quiz1.setId(1L);
        quiz1.setTitle("Quiz 1");

        List<QuizModel> quizzes = List.of(quiz1);

        String token = "valid_token";

        when(quizService.getUserQuizzes(token)).thenReturn(quizzes);

        mockMvc.perform(get("/quizzes/my")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Quiz 1"));

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
        createdQuiz.setCategory(new CategoryModel("art"));

        when(quizService.addQuiz(quizDto, token)).thenReturn(createdQuiz);

        mockMvc.perform(post("/quizzes")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quizDto)));

        verify(quizService).addQuiz(quizDto, token);
    }

    @Test
    @WithMockUser()
    public void testDeleteQuiz_ValidTokenAndQuizId_ShouldReturnNoContent() throws Exception {
        long quizId = 1L;
        String token = "valid_token";

        mockMvc.perform(delete("/quizzes/{quizId}", quizId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(quizService).deleteQuiz(quizId, token);
    }

    @Test
    @WithMockUser()
    public void testPatchQuiz_ValidTokenQuizIdAndQuizPatchDto_ShouldReturnUpdatedQuiz() throws Exception {
        long quizId = 1L;
        String token = "valid_token";

        QuizPatchDto quizPatchDto = new QuizPatchDto("Updated Quiz", "desc", 1L);

        QuizModel updatedQuiz = new QuizModel();
        updatedQuiz.setId(quizId);
        updatedQuiz.setTitle("Updated Quiz");
        updatedQuiz.setCategory(new CategoryModel("art"));

        when(quizService.updateQuiz(quizId, quizPatchDto, token)).thenReturn(updatedQuiz);

        mockMvc.perform(patch("/quizzes/{quizId}", quizId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quizPatchDto)))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Quiz"));

        verify(quizService).updateQuiz(quizId, quizPatchDto, token);
    }

    @Test
    @WithMockUser()
    public void testAddQuestionToQuiz_ValidTokenQuizIdAndQuestionDto_ShouldReturnCreatedQuestion() throws Exception {
        long quizId = 1L;
        String token = "valid_token";

        QuestionDto questionDto = new QuestionDto("Question text");

        QuestionModel createdQuestion = new QuestionModel();
        createdQuestion.setId(1L);
        createdQuestion.setContent("Question text");

        when(quizService.addQuestionToQuiz(quizId, questionDto, token)).thenReturn(createdQuestion);

        mockMvc.perform(post("/quizzes/{quizId}/questions", quizId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(questionDto)))
                .andExpect(jsonPath("$.content").value("Question text"));

        verify(quizService).addQuestionToQuiz(quizId, questionDto, token);
    }

    @Test
    @WithMockUser()
    public void testRemoveQuestionFromQuiz_ValidTokenQuizIdAndQuestionOrdNum_ShouldReturnNoContent() throws Exception {
        long quizId = 1L;
        int questionOrdNum = 1;
        String token = "valid_token";

        mockMvc.perform(delete("/quizzes/{quizId}/questions/{questionOrdNum}", quizId, questionOrdNum)
                .with(csrf()) // Dodanie CSRF Tokena do żądania
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(quizService).removeQuestionFromQuiz(quizId, questionOrdNum, token);
    }

    @Test
    @WithMockUser()
    public void testAddAnswerToQuestion_ValidTokenQuizIdQuestionOrdNumAndAnswerDto_ShouldReturnCreatedAnswer() throws Exception {
        long quizId = 1L;
        int questionOrdNum = 1;
        String token = "valid_token";

        AnswerDto answerDto = new AnswerDto("Answer text", 0);

        AnswerModel createdAnswer = new AnswerModel();
        createdAnswer.setId(1L);
        createdAnswer.setText("Answer text");

        when(quizService.addAnswerToQuestion(quizId, questionOrdNum, answerDto, token)).thenReturn(createdAnswer);

        mockMvc.perform(post("/quizzes/{quizId}/questions/{questionOrdNum}/answers", quizId, questionOrdNum)
                        .with(csrf()) // Dodanie CSRF Tokena do żądania
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(answerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Answer text"));

        verify(quizService).addAnswerToQuestion(quizId, questionOrdNum, answerDto, token);
    }

    @Test
    @WithMockUser
    public void testRemoveAnswerFromQuestion_ValidTokenQuizIdQuestionOrdNumAndAnswerOrdNum_ShouldReturnNoContent() throws Exception {
        long quizId = 1L;
        int questionOrdNum = 1;
        int answerOrdNum = 1;
        String token = "valid_token";

        mockMvc.perform(delete("/quizzes/{quizId}/questions/{questionOrdNum}/answers/{answerOrdNum}", quizId, questionOrdNum, answerOrdNum)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(quizService).removeAnswerFromQuestion(quizId, questionOrdNum, answerOrdNum, token);
    }
}
