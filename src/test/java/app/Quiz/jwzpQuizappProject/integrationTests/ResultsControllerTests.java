package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.CategoryController;
import app.Quiz.jwzpQuizappProject.controllers.ResultsController;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultsController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(ResultsController.class)
public class ResultsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultsService resultsService;

    @Test
    @WithMockUser
    public void testGetMyResultsForQuiz_ShouldReturnResultsList() throws Exception {
        List<ResultsModel> resultsList = new ArrayList<>();
        ResultsModel result1 = new ResultsModel();
        result1.setId(1L);
        ResultsModel result2 = new ResultsModel();
        result2.setId(2L);
        resultsList.add(result1);
        resultsList.add(result2);

        when(resultsService.getAllMyResults("token")).thenReturn(resultsList);

        mockMvc.perform(get("/results/my")
                        .header(HttpHeaders.AUTHORIZATION, "token")).andExpect(status().isOk()).andDo(print());

        verify(resultsService).getAllMyResults("token");
    }

    @Test
    @WithMockUser
    public void testGetSingleResult_ShouldReturnSingleResult() throws Exception {
        ResultsModel result = new ResultsModel();
        result.setId(1L);
        result.setScore(80);

        when(resultsService.getSingleResult(1L, "token")).thenReturn(result);

        mockMvc.perform(get("/results/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "token"));

        verify(resultsService).getSingleResult(1L, "token");
    }
}
