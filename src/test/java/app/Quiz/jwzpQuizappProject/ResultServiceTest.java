package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionStatus;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.*;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
    public void testCreateResults_withValidData_shouldCreateResults() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(1);
        questionAndAnswer.setUserAnswerOrdNum(1);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        AnswerModel answer1 = new AnswerModel();
        AnswerModel answer2 = new AnswerModel();
        answer1.setScore(999);
        answer1.setOrdNum(1);
        answer2.setOrdNum(2);

        QuestionModel questionQuizOne = new QuestionModel();
        questionQuizOne.setOrdNum(1);
        questionQuizOne.setQuestionStatus( QuestionStatus.VALID);
        questionQuizOne.addAnswer(answer1);
        questionQuizOne.addAnswer(answer2);
        var questionsForQuizOne = List.of(questionQuizOne);

        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setQuestions(questionsForQuizOne);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        ResultsModel result = resultsService.createResults(newResults, token);

        assertNotNull(result);
        assertEquals(user, result.getOwner());
        assertEquals(999, result.getScore());
        assertEquals(1, result.getQuizzesResults().size());
        assertEquals(1L, result.getQuizzesResults().iterator().next().getQuizId());

        verify(tokenService, times(1)).getUserFromToken(token);
        verify(quizRepository, times(1)).findById(1L);
        verify(resultsRepository, times(1)).save(result);
        verify(quizResultsRepository, times(1)).save(quizResults);
        verify(questionAndUsersAnswerRepository, times(1)).save(questionAndAnswer);
    }


    @Test
    public void testCreateResults_answerOrdNumIsZero_shouldThrowAnswerNotFoundException() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(1);
        questionAndAnswer.setUserAnswerOrdNum(0);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        AnswerModel answer1 = new AnswerModel();
        AnswerModel answer2 = new AnswerModel();
        answer1.setScore(999);
        answer1.setOrdNum(1);
        answer2.setOrdNum(2);

        QuestionModel questionQuizOne = new QuestionModel();
        questionQuizOne.setOrdNum(1);
        questionQuizOne.setQuestionStatus( QuestionStatus.VALID);
        questionQuizOne.addAnswer(answer1);
        questionQuizOne.addAnswer(answer2);
        var questionsForQuizOne = List.of(questionQuizOne);

        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setQuestions(questionsForQuizOne);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(AnswerNotFoundException.class, () -> resultsService.createResults(newResults, token));
    }

    @Test
    public void testCreateResults_questionOrdNumIsZero_shouldThrowQuestionNotFoundException() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(0);
        questionAndAnswer.setUserAnswerOrdNum(1);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        AnswerModel answer1 = new AnswerModel();
        AnswerModel answer2 = new AnswerModel();
        answer1.setScore(999);
        answer1.setOrdNum(1);
        answer2.setOrdNum(2);

        QuestionModel questionQuizOne = new QuestionModel();
        questionQuizOne.setOrdNum(1);
        questionQuizOne.setQuestionStatus( QuestionStatus.VALID);
        questionQuizOne.addAnswer(answer1);
        questionQuizOne.addAnswer(answer2);
        var questionsForQuizOne = List.of(questionQuizOne);

        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setQuestions(questionsForQuizOne);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(QuestionNotFoundException.class, () -> resultsService.createResults(newResults, token));
    }

    @Test
    public void testCreateResults_answerOrdNumIsTooBig_shouldThrowAnswerNotFoundException() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(1);
        questionAndAnswer.setUserAnswerOrdNum(3);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        AnswerModel answer1 = new AnswerModel();
        AnswerModel answer2 = new AnswerModel();
        answer1.setScore(999);
        answer1.setOrdNum(1);
        answer2.setOrdNum(2);

        QuestionModel questionQuizOne = new QuestionModel();
        questionQuizOne.setOrdNum(1);
        questionQuizOne.setQuestionStatus( QuestionStatus.VALID);
        questionQuizOne.addAnswer(answer1);
        questionQuizOne.addAnswer(answer2);
        var questionsForQuizOne = List.of(questionQuizOne);

        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setQuestions(questionsForQuizOne);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(AnswerNotFoundException.class, () -> resultsService.createResults(newResults, token));
    }

    @Test
    public void testCreateResults_questionOrdNumIsTooBig_shouldThrowQuestionNotFoundException() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(3);
        questionAndAnswer.setUserAnswerOrdNum(1);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        AnswerModel answer1 = new AnswerModel();
        AnswerModel answer2 = new AnswerModel();
        answer1.setScore(999);
        answer1.setOrdNum(1);
        answer2.setOrdNum(2);

        QuestionModel questionQuizOne = new QuestionModel();
        questionQuizOne.setOrdNum(1);
        questionQuizOne.setQuestionStatus( QuestionStatus.VALID);
        questionQuizOne.addAnswer(answer1);
        questionQuizOne.addAnswer(answer2);
        var questionsForQuizOne = List.of(questionQuizOne);

        QuizModel quiz = new QuizModel();
        quiz.setId(1L);
        quiz.setQuestions(questionsForQuizOne);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(QuestionNotFoundException.class, () -> resultsService.createResults(newResults, token));
    }

    @Test
    public void testCreateResults_withInvalidQuizId_shouldThrowQuizNotFoundException() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(0);
        questionAndAnswer.setUserAnswerOrdNum(0);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(QuizNotFoundException.class, () -> resultsService.createResults(newResults, token));

        verify(tokenService, times(1)).getUserFromToken(token);
        verify(quizRepository, times(1)).findById(1L);
        verify(resultsRepository, never()).save(any());
        verify(quizResultsRepository, never()).save(any());
        verify(questionAndUsersAnswerRepository, never()).save(any());
    }

    @Test
    public void testCreateResults_QuestionNotFound_shouldThrowQuestionNotFoundException() throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        UserModel user = makeTokenServiceReturnUser();

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setQuizId(1L);
        QuestionAndUsersAnswerModel questionAndAnswer = new QuestionAndUsersAnswerModel();
        questionAndAnswer.setQuestionOrdNum(0);
        questionAndAnswer.setUserAnswerOrdNum(0);
        Set<QuestionAndUsersAnswerModel> questionAndAnswerSet = new HashSet<>();
        questionAndAnswerSet.add(questionAndAnswer);
        quizResults.setQuestionsAndAnswers(questionAndAnswerSet);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        ResultsDto newResults = new ResultsDto(quizResultsSet, user, LocalDateTime.of(2023,1,1,13,0,0), 0);

        when(quizRepository.findById(1L)).thenReturn(Optional.empty());


        assertThrows(QuizNotFoundException.class, () -> resultsService.createResults(newResults, token));
        verify(tokenService, times(1)).getUserFromToken(token);
        verify(quizRepository, times(1)).findById(1L);
        verify(resultsRepository, never()).save(any());
        verify(quizResultsRepository, never()).save(any());
        verify(questionAndUsersAnswerRepository, never()).save(any());
    }


    @Test
    public void testDeleteSingleResult() {
        ResultsModel result = new ResultsModel();
        doNothing().when(resultsRepository).delete(result);

        resultsService.deleteSingleResult(result);

        verify(resultsRepository, times(1)).delete(result);
    }

    @Test
    public void testDeleteAllResults() {
        List<ResultsModel> results = new ArrayList<>();
        doNothing().when(resultsRepository).deleteAll(results);

        resultsService.deleteAllResults(results);

        verify(resultsRepository, times(1)).deleteAll(results);
    }

    @Test
    public void testUpdateQuestionAndUsersAnswer_withValidData_shouldUpdateQuestionAndUsersAnswer() throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException {
        QuestionAndUsersAnswerPatchDto patchDto = new QuestionAndUsersAnswerPatchDto(1L, 2L, 1,1);

        QuestionAndUsersAnswerModel originalQuestionAndUsersAnswer = new QuestionAndUsersAnswerModel();
        originalQuestionAndUsersAnswer.setId(1L);
        originalQuestionAndUsersAnswer.setUserAnswerOrdNum(1L);

        QuizModel quiz = new QuizModel();
        quiz.setId(2L);

        QuestionModel question = new QuestionModel();
        question.setOrdNum(1);

        AnswerModel answer = new AnswerModel();
        answer.setOrdNum(1);

        question.setAnswers(List.of(answer));

        quiz.setQuestions(List.of(question));

        when(questionAndUsersAnswerRepository.findById(1L)).thenReturn(Optional.of(originalQuestionAndUsersAnswer));
        when(quizRepository.findById(2L)).thenReturn(Optional.of(quiz));
        when(questionAndUsersAnswerRepository.save(originalQuestionAndUsersAnswer)).thenReturn(originalQuestionAndUsersAnswer);

        QuestionAndUsersAnswerModel updatedQuestionAndUsersAnswer = resultsService.updateQuestionAndUsersAnswer(patchDto);

        assertNotNull(updatedQuestionAndUsersAnswer);
        assertEquals(1L, updatedQuestionAndUsersAnswer.getId());
        assertEquals(1L, updatedQuestionAndUsersAnswer.getQuestionOrdNum());

        verify(questionAndUsersAnswerRepository, times(1)).findById(1L);
        verify(quizRepository, times(1)).findById(2L);
        verify(questionAndUsersAnswerRepository, times(1)).save(originalQuestionAndUsersAnswer);
    }


    @Test
    public void testUpdateQuizResults_withValidData_shouldUpdateQuizResults() throws AnswerNotFoundException, QuizNotFoundException {
        QuizResultsPatchDto patchDto = new QuizResultsPatchDto(1L, 2L, 100L);

        QuizResultsModel originalQuizResults = new QuizResultsModel();
        originalQuizResults.setQuizId(1L);
        originalQuizResults.setScore(50);

        QuizModel quiz = new QuizModel();
        quiz.setId(2L);

        QuestionModel question = new QuestionModel();
        question.setOrdNum(1);

        AnswerModel answer = new AnswerModel();
        answer.setOrdNum(1);

        question.addAnswer(answer);

        quiz.addQuestion(question);

        when(quizResultsRepository.findById(1L)).thenReturn(Optional.of(originalQuizResults));
        when(quizRepository.findById(2L)).thenReturn(Optional.of(quiz));
        when(quizResultsRepository.save(originalQuizResults)).thenReturn(originalQuizResults);

        QuizResultsModel updatedQuizResults = resultsService.updateQuizResults(patchDto);

        assertNotNull(updatedQuizResults);
        assertEquals(2L, updatedQuizResults.getQuizId());
        assertEquals(100, updatedQuizResults.getScore());

        verify(quizRepository, times(1)).findById(2L);
        verify(quizResultsRepository, times(1)).save(originalQuizResults);
    }


    @Test
    public void testUpdateResults_withValidData_shouldUpdateResults() throws ResultNotFoundException, RoomNotFoundException {
        ResultsPatchDto patchDto = new ResultsPatchDto(1L, 2L, 99L, 3L);

        ResultsModel originalResults = new ResultsModel();
        originalResults.setId(1L);
        originalResults.setScore(50);

        RoomModel room = new RoomModel();
        room.setId(2L);

        UserModel owner = new UserModel();
        owner.setId(3L);

        when(resultsRepository.findById(1L)).thenReturn(Optional.of(originalResults));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));
        when(userRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(resultsRepository.save(originalResults)).thenReturn(originalResults);

        ResultsModel updatedResults = resultsService.updateResults(patchDto);

        assertNotNull(updatedResults);
        assertEquals(1L, updatedResults.getId());
        assertEquals(2L, updatedResults.getRoom().getId());
        assertEquals(3L, updatedResults.getOwner().getId());
        assertEquals(99, updatedResults.getScore());

        verify(roomRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).findById(3L);
        verify(resultsRepository, times(1)).save(originalResults);
    }


    @Test
    public void testDeleteQuestionAndAnswer() throws AnswerNotFoundException {
        long qaaId = 1L;
        long quizResultsId = 2L;

        QuestionAndUsersAnswerModel qaa = new QuestionAndUsersAnswerModel();
        qaa.setId(qaaId);
        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setId(quizResultsId);

        var qaas = new HashSet<QuestionAndUsersAnswerModel>();
        qaas.add(qaa);
        quizResults.setQuestionsAndAnswers(qaas);

        when(questionAndUsersAnswerRepository.findById(qaaId)).thenReturn(Optional.of(qaa));
        when(quizResultsRepository.findById(quizResultsId)).thenReturn(Optional.of(quizResults));

        resultsService.deleteQuestionAndAnswer(qaaId, quizResultsId);

        verify(quizResultsRepository, times(1)).save(quizResults);
        verify(questionAndUsersAnswerRepository, times(1)).delete(qaa);
    }




    @Test
    public void deleteSingleResult_resultExists_removesResult(){
        var results = new ResultsModel();
        resultsService.deleteSingleResult(results);
        verify(resultsRepository, times(1)).delete(results);

    }


    @Test
    public void testDeleteQuizResults() throws AnswerNotFoundException, ResultNotFoundException {
        long quizResultsId = 1L;
        long resultsId = 2L;

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setId(quizResultsId);

        ResultsModel results = new ResultsModel();
        results.setId(resultsId);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        results.setQuizzesResults(quizResultsSet);

        when(quizResultsRepository.findById(quizResultsId)).thenReturn(Optional.of(quizResults));
        when(resultsRepository.findById(resultsId)).thenReturn(Optional.of(results));

        resultsService.deleteQuizResults(quizResultsId, resultsId);

        verify(resultsRepository, times(1)).save(results);
        verify(quizResultsRepository, times(1)).delete(quizResults);
    }


    @Test
    public void testDeleteQuizResults_withAnswerNotFoundException() throws AnswerNotFoundException, ResultNotFoundException {
        long quizResultsId = 1L;
        long resultsId = 2L;

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setId(quizResultsId);

        ResultsModel results = new ResultsModel();
        results.setId(resultsId);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        results.setQuizzesResults(quizResultsSet);

        when(quizResultsRepository.findById(quizResultsId)).thenReturn(Optional.empty());
        when(resultsRepository.findById(resultsId)).thenReturn(Optional.of(results));

        assertThrows(AnswerNotFoundException.class, () -> resultsService.deleteQuizResults(quizResultsId, resultsId));

        verify(resultsRepository, never()).save(results);
        verify(quizResultsRepository, never()).delete(quizResults);
    }

    @Test
    public void testDeleteQuizResults_withResultNotFoundException() throws AnswerNotFoundException, ResultNotFoundException {
        long quizResultsId = 1L;
        long resultsId = 2L;

        QuizResultsModel quizResults = new QuizResultsModel();
        quizResults.setId(quizResultsId);

        ResultsModel results = new ResultsModel();
        results.setId(resultsId);
        Set<QuizResultsModel> quizResultsSet = new HashSet<>();
        quizResultsSet.add(quizResults);
        results.setQuizzesResults(quizResultsSet);

        when(quizResultsRepository.findById(quizResultsId)).thenReturn(Optional.of(quizResults));
        when(resultsRepository.findById(resultsId)).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> resultsService.deleteQuizResults(quizResultsId, resultsId));

        verify(resultsRepository, never()).save(results);
        verify(quizResultsRepository, never()).delete(quizResults);
    }


    @Test
    public void testDeleteResults_withValidData_shouldDeleteResults() throws ResultNotFoundException {
        long resultsId = 1L;

        ResultsModel results = new ResultsModel();
        results.setId(resultsId);

        when(resultsRepository.findById(resultsId)).thenReturn(Optional.of(results));

        resultsService.deleteResults(resultsId);

        verify(resultsRepository, times(1)).delete(results);
    }

    @Test
    public void testDeleteResults_withInvalidData_shouldThrowResultNotFoundException() {
        long resultsId = 1L;

        when(resultsRepository.findById(resultsId)).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> resultsService.deleteResults(resultsId));

        verify(resultsRepository, never()).delete(any());
    }

    private UserModel makeTokenServiceReturnUser(){
        long userId = 1;
        var user = new UserModel();
        user.setId(userId);

        when(tokenService.getUserFromToken(token)).thenReturn(user);

        return user;

    }


}
