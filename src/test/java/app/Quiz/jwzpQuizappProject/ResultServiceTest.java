package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionStatus;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.QuestionAndUsersAnswerModel;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsDto;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.models.users.UserRole;
import app.Quiz.jwzpQuizappProject.repositories.*;
import app.Quiz.jwzpQuizappProject.service.ResultsService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    public void createResults_createsSuccessfully() throws AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists {
//        var user = makeTokenServiceReturnUser();
//
//        long quizOneId = 1;
//        long quizTwoId = 2;
//
//        QuizModel quizOne = new QuizModel();
//        quizOne.setId(quizOneId);
//        quizOne.setQuestions(new ArrayList<>(1));
//
//        QuizModel quizTwo = new QuizModel();
//        quizTwo.setId(quizOneId);
//
//        QuestionModel questionQuizOne = new QuestionModel();
//        questionQuizOne.setOrdNum(0);
//        questionQuizOne.setQuestionStatus( QuestionStatus.VALID);
//        var questionsForQuizOne = new ArrayList<QuestionModel>(1);
//        questionsForQuizOne.add(questionQuizOne);
//        quizOne.setQuestions(questionsForQuizOne);
//
//        QuestionModel questionQuizTwo = new QuestionModel();
//        questionQuizTwo.setOrdNum(0);
//        questionQuizTwo.setQuestionStatus( QuestionStatus.VALID);
//        var questionsForQuizTwo = new ArrayList<QuestionModel>(1);
//        questionsForQuizTwo.add(questionQuizTwo);
//        quizTwo.setQuestions(questionsForQuizTwo);
//
//        HashSet<QuizResultsModel> quizResults = new HashSet<>(2);
//        QuizResultsModel quizResult1 = new QuizResultsModel();
//        quizResult1.setQuizId(quizOneId);
//
//        long questionsOrdNumForQuizOne = 0;
//        long answerOrdNumForQuizOne = 0;
//        HashSet<QuestionAndUsersAnswerModel> qaasForQuizOne = new HashSet<>(1);
//        var qaaForQuizOne = new QuestionAndUsersAnswerModel();
//        qaaForQuizOne.setQuestionOrdNum(questionsOrdNumForQuizOne);
//        qaaForQuizOne.setUserAnswerOrdNum(answerOrdNumForQuizOne);
//        qaasForQuizOne.add(qaaForQuizOne);
//
//
//        long questionsOrdNumForQuizTwo = 0;
//        long answerOrdNumForQuizTwo = 0;
//        HashSet<QuestionAndUsersAnswerModel> qaasForQuizTwo = new HashSet<>(1);
//        var qaaForQuizTwo = new QuestionAndUsersAnswerModel();
//        qaaForQuizOne.setQuestionOrdNum(questionsOrdNumForQuizTwo);
//        qaaForQuizOne.setUserAnswerOrdNum(answerOrdNumForQuizTwo);
//        qaasForQuizTwo.add(qaaForQuizTwo);
//
//        var quizOneResults = new QuizResultsModel();
//        quizOneResults.setQuizId(quizOneId);
//        quizOneResults.setQuestionsAndAnswers(qaasForQuizOne);
//
//        var quizTwoResults = new QuizResultsModel();
//        quizTwoResults.setQuizId(quizTwoId);
//        quizTwoResults.setQuestionsAndAnswers(qaasForQuizTwo);
//
//
//        quizResults.add(quizOneResults);
//        quizResults.add(quizTwoResults);
//
//
//        var resultsDto = new ResultsDto(quizResults, user, LocalDateTime.of(2023,1,15,13,0,0),0 );
//
//        when( quizRepository.findById(quizOneId)).thenReturn(Optional.of(quizOne));
//        when( quizRepository.findById(quizTwoId)).thenReturn(Optional.of(quizTwo));
//
//        resultsService.createResults(resultsDto,token);
//
//        verify(resultsRepository, times(1)).save(any(ResultsModel.class));
        //Nie mam pojecia czm to sie wywala na dodaniu question do quiz
    }

    @Test
    public void createResultsForRoom_createsSuccessfully(){

    }

    @Test
    public void deleteSingleResult_resultExists_removesResult(){
        var results = new ResultsModel();
        resultsService.deleteSingleResult(results);
        verify(resultsRepository, times(1)).delete(results);

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
