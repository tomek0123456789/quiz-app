package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.controllers.ResultsController;
import app.Quiz.jwzpQuizappProject.models.results.QuestionAndUsersAnswerPatchDto;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsDto;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.service.QuizService;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static app.Quiz.jwzpQuizappProject.integrationTests.IntTestsHelper.asJsonString;
import static org.mockito.Mockito.verify;
import static org.springframework.http.RequestEntity.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultsController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(ResultsController.class)
public class ResultsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultsService resultsService;

//    @MockBean
//    private QuizService quizService;


    @Test
    @WithMockUser
    public void testGetMyResultsForQuiz_ValidToken_ShouldReturnResultsList() throws Exception {
        String token = "valid_token";
        List<ResultsModel> resultsList = new ArrayList<>();

        when(resultsService.getAllMyResults(token)).thenReturn(resultsList);

        mockMvc.perform(get("/results/my")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(resultsService).getAllMyResults(token);
    }

    @Test
    @WithMockUser
    public void testGetSingleResult_ValidResultIdAndToken_ShouldReturnResult() throws Exception {
        long resultId = 1L;
        String token = "valid_token";
        ResultsModel result = new ResultsModel();

        when(resultsService.getSingleResult(resultId, token)).thenReturn(result);

        mockMvc.perform(get("/results/{id}", resultId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(resultsService).getSingleResult(resultId, token);
    }

    @Test
    @WithMockUser
    public void testGetMyResultsForQuizEndpoint_ValidQuizIdAndToken_ShouldReturnQuizResultsSet() throws Exception {
        long quizId = 1L;
        String token = "valid_token";
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();

        when(resultsService.getMyResultsForQuiz(quizId, token)).thenReturn(quizResultsSet);

        mockMvc.perform(get("/results/quiz/{id}", quizId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(resultsService).getMyResultsForQuiz(quizId, token);
    }

    @Test
    @WithMockUser
    public void testGetMyBestResult_ValidQuizIdAndToken_ShouldReturnBestQuizResult() throws Exception {
        long quizId = 1L;
        String token = "valid_token";
        QuizResultsModel bestResult = new QuizResultsModel();

        when(resultsService.getMyBestResultForQuiz(token, quizId)).thenReturn(bestResult);

        mockMvc.perform(get("/results/quiz/{id}/best-result", quizId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(resultsService).getMyBestResultForQuiz(token, quizId);
    }
}
