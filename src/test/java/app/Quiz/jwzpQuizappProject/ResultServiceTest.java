package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ResultServiceTest {

    private ResultsService resultsService;

    @Mock
    private QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    @Mock
    private QuizResultsRepository quizResultsRepository;
    @Mock
    private ResultsRepository resultsRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private RoomAuthoritiesValidator roomAuthoritiesValidator;
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resultsService = new ResultsService(
                questionAndUsersAnswerRepository,
                quizResultsRepository,
                resultsRepository,
                quizRepository,
                tokenService,
                roomAuthoritiesValidator,
                roomRepository,
                userRepository
        );
    }

    @Test
    public  void getMyResultsForQuiz(){
        String token = "Baerer token";
        long userId = 1;
        var authorizedUser = new UserModel();
        authorizedUser.setId(userId);

        long quizId = 1;
        QuizModel quizModel = new QuizModel();
        quizModel.setId(quizId);

        when(tokenService.getUserFromToken(token)).thenReturn(authorizedUser);


        var result = new ResultsModel();
        result.setOwner(authorizedUser);
        QuizResultsModel quizResults1 = new QuizResultsModel();
        quizResults1.setQuiz(quizModel);
        var quizzesResults = new HashSet<QuizResultsModel>();
        quizzesResults.add(quizResults1);
        result.setQuizzesResults(quizzesResults);

        when(resultsRepository.findAllByOwnerAndQuizzesResultsId(userId, quizId)).thenReturn(quizzesResults);

        var myResults = this.resultsService.getMyResultsForQuiz(quizId, token);

        assertEquals(1, myResults.size());
        assertEquals(quizzesResults, myResults);
    }

    @Test
    public void getMyBestResultForQuiz_DifferentScores_returnsOneResult(){

    }

    @Test
    public void getMyBestResultForQuiz_DrawInScores_returnsOneResult(){

    }

    @Test
    public void getAllMyResults_ReturnsAllResults(){

    }

    @Test
    public void getAllMyResults_NoResults_ThrowsException(){
        //TODO
    }

    @Test
    public void getResultsForRoom_NoRoomWIthId_ThrowsException(){

    }

    @Test
    public void getResultsForRoom_NoResultsWithRoomId_ThrowsException(){

    }

    @Test
    public void getResultsForRoom_OneResultForRoom_ReturnsOneResult(){

    }

    @Test
    public void getSingleResult_idExists_returnsOneResult(){

    }

    @Test
    public void getSingleResult_idDoesNotExist_throwsException(){

    }

    @Test
    public void createResults_createsSuccessfully(){

    }

    @Test
    public void createResultsForRoom_createsSuccessfully(){

    }

    @Test
    public void deleteSingleResult_resultExists_removesResult(){

    }

    @Test
    public void updateQuestionAndUsersAnswer_updateAllAvailableProperties_ReturnsUpdatedQaa(){

    }

    @Test
    public void updateQuizResults_updateAllAvailableProperties_ReturnsUpdatedQuizResult(){

    }

    @Test
    public void updateResults_updateAllAvailableProperties_ReturnsUpdatedResults(){

    }

    @Test
    public void deleteQuestionAndAnswer_DeletesQaa(){

    }

    @Test
    public void deleteQuizResults_DeletesQuizResult(){

    }

    @Test
    public void deleteResults_DeletesResult(){

    }


}
