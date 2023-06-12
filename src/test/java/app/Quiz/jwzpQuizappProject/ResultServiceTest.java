package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserRole;
import app.Quiz.jwzpQuizappProject.repositories.*;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ResultServiceTest {

    private ResultsService resultsService;
    private String token = "Baerer token";

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
        long userId = 1;
        var authorizedUser = new UserModel();
        authorizedUser.setId(userId);

        long quizId = 1;
        QuizModel quizModel = new QuizModel();
        quizModel.setId(quizId);

        when(tokenService.getUserFromToken(token)).thenReturn(authorizedUser);

        QuizResultsModel quizResults1 = new QuizResultsModel();
        quizResults1.setQuiz(quizModel);
        quizResults1.setScore(0);

        QuizResultsModel quizResultWithBetterScore = new QuizResultsModel();
        quizResultWithBetterScore.setQuiz(quizModel);
        quizResultWithBetterScore.setScore(1);

        var results = new HashSet<QuizResultsModel>();
        results.add(quizResults1);
        results.add(quizResultWithBetterScore);

        when(resultsRepository.findAllByOwnerAndQuizzesResultsId(userId, quizId)).thenReturn(results);

        var bestResult = resultsService.getMyBestResultForQuiz(token, quizId);

//        assertEquals(1, bestResult.size());
        assertEquals(quizResultWithBetterScore, bestResult);
    }

    @Test
    public void getMyBestResultForQuiz_DrawInScores_returnsOneResult(){
        long userId = 1;
        var authorizedUser = new UserModel();
        authorizedUser.setId(userId);

        long quizId = 1;
        QuizModel quizModel = new QuizModel();
        quizModel.setId(quizId);
        
        int bestScore = 1;

        when(tokenService.getUserFromToken(token)).thenReturn(authorizedUser);

        QuizResultsModel quizResultWithScoreBestScore = new QuizResultsModel();
        quizResultWithScoreBestScore.setQuiz(quizModel);
        quizResultWithScoreBestScore.setScore(bestScore);

        QuizResultsModel anotherQuizResultWithScoreBestScore = new QuizResultsModel();
        anotherQuizResultWithScoreBestScore.setQuiz(quizModel);
        anotherQuizResultWithScoreBestScore.setScore(bestScore);

        QuizResultsModel quizResultWithScore0 = new QuizResultsModel();
        quizResultWithScore0.setQuiz(quizModel);
        quizResultWithScore0.setScore(1);

        var results = new HashSet<QuizResultsModel>();
        results.add(quizResultWithScore0);
        results.add(quizResultWithScoreBestScore);
        results.add(anotherQuizResultWithScoreBestScore);

        when(resultsRepository.findAllByOwnerAndQuizzesResultsId(userId, quizId)).thenReturn(results);

        var bestResult = resultsService.getMyBestResultForQuiz(token, quizId);

        assertEquals(bestScore, bestResult.getScore());
    }

    @Test
    public void getAllMyResults_ReturnsAllResults(){
        long userId = 1;
        var authorizedUser = new UserModel();
        authorizedUser.setId(userId);
        when(tokenService.getUserFromToken(token)).thenReturn(authorizedUser);

        var expectedResults = new ArrayList<ResultsModel>();

        when(resultsRepository.findAllByOwner(authorizedUser)).thenReturn(expectedResults);

        var actualResults = resultsService.getAllMyResults(token);

        assertEquals(expectedResults, actualResults);
    }

    @Test
    public void getResultsForRoom_NoRoomWIthId_ThrowsRoomNotFoundException() throws RoomNotFoundException, PermissionDeniedException {
        long roomId = 1;

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () ->resultsService.getResultsForRoom(roomId, token));
    }

    @Test
    public void getResultsForRoom_NoAuthorized_ThrowsException(){
        long roomId = 1;

        var room = new RoomModel();
        room.setOwner(new UserModel());

        long userId = 1;
        var notAuthorizedUser = new UserModel();
        notAuthorizedUser.setId(userId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(tokenService.getUserFromToken(token)).thenReturn(notAuthorizedUser);

        assertThrows(PermissionDeniedException.class, () ->resultsService.getResultsForRoom(roomId, token));
    }

    @Test
    public void getResultsForRoom_userAuthorizedAuthorized_ReturnsOneResult() throws RoomNotFoundException, PermissionDeniedException {

        long roomId = 1;
        var room = new RoomModel();
        room.setOwner(new UserModel());
        room.setId(roomId);

        var adminUser = new UserModel();

        var expectedResults = new ArrayList<ResultsModel>();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(tokenService.getUserFromToken(token)).thenReturn(adminUser);
        when(resultsRepository.findByRoomId(roomId)).thenReturn(expectedResults);
        when(roomAuthoritiesValidator.validateUserRoomInfoAuthorities(adminUser,room)).thenReturn(true);

        var actualResultsForRoom = resultsService.getResultsForRoom(roomId, token);

        assertEquals(expectedResults, actualResultsForRoom);
    }

    @Test
    public void getSingleResult_idExistsUserOwnsResult_returnsOneResult() throws PermissionDeniedException, ResultNotFoundException {
        var user = makeTokenServiceReturnUser();

        long resultId = 1;

        var expectedResult = new ResultsModel();
        expectedResult.setOwner(user);

        when( resultsRepository.findById(resultId)).thenReturn(Optional.of(expectedResult));
        var actualResult = resultsService.getSingleResult(resultId, token);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getSingleResult_idExistsUserIsAdmin_returnsOneResult() throws PermissionDeniedException, ResultNotFoundException {
        var user = makeTokenServiceReturnUser();
        var adminRoles = new ArrayList<UserRole>();
        adminRoles.add(UserRole.ADMIN);
        user.setRoles(adminRoles);

        long resultId = 1;

        var expectedResult = new ResultsModel();

        when( resultsRepository.findById(resultId)).thenReturn(Optional.of(expectedResult));
        var actualResult = resultsService.getSingleResult(resultId, token);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getSingleResult_idExistsUserNotAuthorized_throwsPermissionDeniedException() throws PermissionDeniedException, ResultNotFoundException {
        var user = makeTokenServiceReturnUser();
        var adminRoles = new ArrayList<UserRole>();
        user.setRoles(adminRoles);

        long resultId = 1;

        var expectedResult = new ResultsModel();

        when( resultsRepository.findById(resultId)).thenReturn(Optional.of(expectedResult));

        assertThrows(PermissionDeniedException.class, () ->resultsService.getSingleResult(resultId, token));
    }

    @Test
    public void getSingleResult_idDoesNotExist_throwsException() throws PermissionDeniedException, ResultNotFoundException {
        makeTokenServiceReturnUser();
        long resultId = 1;
        when( resultsRepository.findById(resultId)).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> resultsService.getSingleResult(resultId, token));
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

    private UserModel makeTokenServiceReturnUser(){
        long userId = 1;
        var user = new UserModel();
        user.setId(userId);

        when(tokenService.getUserFromToken(token)).thenReturn(user);

        return user;

    }


}
