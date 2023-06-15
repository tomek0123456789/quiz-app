package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswersLimitException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.categories.CategoryNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionsLimitException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerDto;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionDto;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizPatchDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizStatus;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.AnswerRepository;
import app.Quiz.jwzpQuizappProject.repositories.QuestionRepository;
import app.Quiz.jwzpQuizappProject.repositories.QuizRepository;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import app.Quiz.jwzpQuizappProject.service.QuizService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;


import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



public class QuizServiceTest {

    private final String token = "bearer token";
    private QuizService quizService;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TokenService tokenService;
    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        quizService = new QuizService(
               answerRepository, questionRepository, quizRepository, categoryService, tokenService, clock
        );
    }

    @Test
    public void testGetSingleQuiz_withValidQuizId_shouldReturnQuiz() throws QuizNotFoundException {
        long quizId = 1L;

        QuizModel quiz = new QuizModel();
        quiz.setId(quizId);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizModel result = quizService.getSingleQuiz(quizId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(quizId,result.getId());

        verify(quizRepository, times(1)).findById(quizId);
    }

    @Test
    public void testGetSingleQuiz_withInvalidQuizId_shouldThrowQuizNotFoundException() {
        long quizId = 1L;

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.getSingleQuiz(quizId));

        verify(quizRepository, times(1)).findById(quizId);
    }

    @Test
    public void testGetMultipleQuizzes_withNoFilters_shouldReturnAllQuizzes() {
        List<QuizModel> quizzes = List.of(
                new QuizModel(),
                new QuizModel(),
                new QuizModel()
        );

        when(quizRepository.findAll()).thenReturn(quizzes);

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.empty(), Optional.empty(), Optional.empty());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(quizzes.size(), result.size());

        verify(quizRepository, times(1)).findAll();
    }

    @Test
    public void testGetMultipleQuizzes_withTitlePartFilter_shouldReturnQuizzesWithMatchingTitlePart() {
        String titlePart = "Java";
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics"),
                new QuizModel(2L, "Java Advanced"),
                new QuizModel(3L, "Python Basics")
        );

        when(quizRepository.findAllByTitleContaining(titlePart)).thenReturn(quizzes.subList(0, 2));

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.of(titlePart), Optional.empty(), Optional.empty());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2L, result.get(1).getId());

        verify(quizRepository, times(1)).findAllByTitleContaining(titlePart);
    }

    @Test
    public void testGetMultipleQuizzes_withCategoryNameFilter_shouldReturnQuizzesWithMatchingCategoryName() {
        String categoryName = "Programming";
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics", new CategoryModel(categoryName)),
                new QuizModel(2L, "Python Basics", new CategoryModel(categoryName)),
                new QuizModel(3L, "Java Advanced", new CategoryModel("Web Development"))
        );

        when(quizRepository.findAllByCategoryName(categoryName)).thenReturn(quizzes.subList(0, 2));

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.empty(), Optional.of(categoryName), Optional.empty());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2L, result.get(1).getId());

        verify(quizRepository, times(1)).findAllByCategoryName(categoryName);
    }

    @Test
    public void testGetMultipleQuizzes_withValidQuizzesFilter_shouldReturnValidQuizzes() {
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics", QuizStatus.VALID),
                new QuizModel(3L, "Java Advanced", QuizStatus.VALID)
        );

        when(quizRepository.findAllByQuizStatus(QuizStatus.VALID)).thenReturn(quizzes.subList(0, 2));

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.empty(), Optional.empty(), Optional.of(true));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(3L, result.get(1).getId());

        verify(quizRepository, times(1)).findAllByQuizStatus(QuizStatus.VALID);
    }

    @Test
    public void testGetMultipleQuizzes_withTitlePartAndCategoryNameFilters_shouldReturnQuizzesWithMatchingTitlePartAndCategoryName() {
        String titlePart = "Java";
        String categoryName = "Programming";
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics", new CategoryModel(categoryName)),
                new QuizModel(2L, "Java Advanced", new CategoryModel(categoryName)),
                new QuizModel(3L, "Python Basics", new CategoryModel(categoryName)),
                new QuizModel(4L, "Java Basics", new CategoryModel("Web Development"))
        );

        when(quizRepository.findAllByTitleContainingAndCategoryName(titlePart, categoryName)).thenReturn(quizzes.subList(0, 2));

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.of(titlePart), Optional.of(categoryName), Optional.empty());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2L, result.get(1).getId());

        verify(quizRepository, times(1)).findAllByTitleContainingAndCategoryName(titlePart, categoryName);
    }

    @Test
    public void testGetMultipleQuizzes_withTitlePartAndValidQuizzesFilters_shouldReturnQuizzesWithMatchingTitlePartAndValidQuizzes() {
        String titlePart = "Java";
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics", QuizStatus.VALID),
                new QuizModel(4L, "Java Basics", QuizStatus.VALID)
        );

        when(quizRepository.findAllByTitleContainingAndQuizStatus(titlePart, QuizStatus.VALID)).thenReturn(quizzes);

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.of(titlePart), Optional.empty(), Optional.of(true));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(4L, result.get(1).getId());

        verify(quizRepository, times(1)).findAllByTitleContainingAndQuizStatus(titlePart, QuizStatus.VALID);
    }

    @Test
    public void testGetMultipleQuizzes_withCategoryNameAndValidQuizzesFilters_shouldReturnQuizzesWithMatchingCategoryNameAndValidQuizzes() {
        String categoryName = "Programming";
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics", new CategoryModel(categoryName), QuizStatus.VALID),
                new QuizModel(3L, "Java Advanced", new CategoryModel(categoryName), QuizStatus.VALID)
        );

        when(quizRepository.findAllByCategoryNameAndQuizStatus(categoryName, QuizStatus.VALID)).thenReturn(quizzes.subList(0, 2));

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.empty(), Optional.of(categoryName), Optional.of(true));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(3L, result.get(1).getId());

        verify(quizRepository, times(1)).findAllByCategoryNameAndQuizStatus(categoryName, QuizStatus.VALID);
    }

    @Test
    public void testGetMultipleQuizzes_withAllFilters_shouldReturnQuizzesWithMatchingTitlePartCategoryNameAndValidQuizzes() {
        String titlePart = "Java";
        String categoryName = "Programming";
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Java Basics", new CategoryModel(categoryName), QuizStatus.VALID),
                new QuizModel(3L, "Python Basics", new CategoryModel(categoryName), QuizStatus.VALID)
        );

        when(quizRepository.findAllByTitleContainingAndCategoryNameAndQuizStatus(titlePart, categoryName, QuizStatus.VALID)).thenReturn(quizzes.subList(0, 1));

        List<QuizModel> result = quizService.getMultipleQuizzes(Optional.of(titlePart), Optional.of(categoryName), Optional.of(true));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());

        verify(quizRepository, times(1)).findAllByTitleContainingAndCategoryNameAndQuizStatus(titlePart, categoryName, QuizStatus.VALID);
    }

    @Test
    public void testGetUserQuizzes_withValidToken_shouldReturnUserQuizzes() {
        UserModel user = new UserModel();
        List<QuizModel> quizzes = List.of(
                new QuizModel(1L, "Quiz 1", user),
                new QuizModel(2L, "Quiz 2", user),
                new QuizModel(3L, "Quiz 3", new UserModel())
        );

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(quizRepository.findAllByOwner(user)).thenReturn(quizzes.subList(0, 2));

        List<QuizModel> result = quizService.getUserQuizzes(token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2L, result.get(1).getId());

        verify(tokenService, times(1)).getUserFromToken(token);
        verify(quizRepository, times(1)).findAllByOwner(user);
    }


    @Test
    public void testAddQuiz_withValidQuizDtoAndToken_shouldReturnAddedQuiz() throws CategoryNotFoundException {
        QuizDto quizDto = new QuizDto("Quiz 1", "Description 1", 1L);

        UserModel user = new UserModel();
        CategoryModel category = new CategoryModel();
        QuizModel quizModel = new QuizModel("Quiz 1", "Description 1", user, category, Instant.now());

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(categoryService.getSingleCategory(quizDto.categoryId())).thenReturn(category);
        when(quizRepository.save(any(QuizModel.class))).thenReturn(quizModel);

        QuizModel result = quizService.addQuiz(quizDto, token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(quizModel.getCategory(), result.getCategory());
        Assertions.assertEquals(quizModel.getTitle(), result.getTitle());
        Assertions.assertEquals(quizModel.getQuizStatus(), result.getQuizStatus());

        verify(tokenService, times(1)).getUserFromToken(token);
        verify(categoryService, times(1)).getSingleCategory(quizDto.categoryId());
        verify(quizRepository, times(1)).save(any(QuizModel.class));
    }

    @Test
    public void testUpdateQuiz_withValidQuizIdAndQuizPatchDtoAndToken_shouldReturnUpdatedQuiz() throws QuizNotFoundException, PermissionDeniedException, CategoryNotFoundException {
        long quizId = 1L;
        QuizPatchDto quizPatchDto = new QuizPatchDto("Updated Title", "Updated Description", 2L, QuizStatus.VALIDATABLE);

        UserModel user = new UserModel();
        CategoryModel category = new CategoryModel();
        QuizModel quizModel = new QuizModel();
        quizModel.setId(quizId);
        quizModel.setTitle("Quiz 1");
        quizModel.setDescription("Description 1");
        quizModel.setOwner(user);
        quizModel.setCategory(category);


        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizModel));
        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(categoryService.getSingleCategory(quizPatchDto.categoryId())).thenReturn(category);
        when(quizRepository.save(any(QuizModel.class))).thenReturn(quizModel);

        QuizModel result = quizService.updateQuiz(quizId, quizPatchDto, token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(quizModel, result);
        Assertions.assertEquals("Updated Title", result.getTitle());
        Assertions.assertEquals("Updated Description", result.getDescription());
        Assertions.assertEquals(category, result.getCategory());

        verify(quizRepository, times(1)).findById(quizId);
        verify(tokenService, times(1)).getUserFromToken(token);
        verify(categoryService, times(1)).getSingleCategory(quizPatchDto.categoryId());
        verify(quizRepository, times(1)).save(any(QuizModel.class));
    }

    @Test
    public void testDeleteQuiz_withValidQuizIdAndToken_shouldDeleteQuiz() throws QuizNotFoundException, PermissionDeniedException {
        long quizId = 1L;
        var user = makeTokenServiceReturnUser();

        QuizModel quizModel = new QuizModel(quizId, "Quiz 1", new UserModel());
        quizModel.setOwner(user);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizModel));

        quizService.deleteQuiz(quizId, token);

        verify(quizRepository, times(1)).findById(quizId);
        verify(quizRepository, times(1)).delete(quizModel);
    }

    @Test
    public void testDeleteQuiz_withNonExistingQuizId_shouldThrowQuizNotFoundException() {
        long quizId = 1L;

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.deleteQuiz(quizId, "valid_token"));

        verify(quizRepository, times(1)).findById(quizId);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    public void testAddQuestionToQuiz_withValidQuizIdAndQuestionDtoAndToken_shouldReturnAddedQuestion() throws PermissionDeniedException, QuestionsLimitException, QuizNotFoundException {
        long quizId = 1L;
        QuestionDto questionDto = new QuestionDto("Question 1");

        var user = makeTokenServiceReturnUser();

        QuizModel quizModel = new QuizModel(quizId, "Quiz 1", new UserModel());
        quizModel.setOwner(user);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizModel));
        when(questionRepository.save(any(QuestionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuestionModel result = quizService.addQuestionToQuiz(quizId, questionDto, token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Question 1", result.getContent());
        Assertions.assertEquals(1, result.getOrdNum());

        verify(quizRepository, times(1)).findById(quizId);
        verify(questionRepository, times(1)).save(any(QuestionModel.class));
        verify(quizRepository, times(1)).save(quizModel);
    }

    @Test
    public void testAddQuestionToQuiz_withNonExistingQuizId_shouldThrowQuizNotFoundException() {
        long quizId = 1L;
        QuestionDto questionDto = new QuestionDto("Question 1");

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.addQuestionToQuiz(quizId, questionDto, "valid_token"));

        verify(quizRepository, times(1)).findById(quizId);
        verifyNoMoreInteractions(questionRepository);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    public void testRemoveQuestionFromQuiz_withQuestionOrdinalNumberExceedingQuizQuestionsLimit_shouldThrowQuestionNotFoundException() {
        long quizId = 1L;
        int questionOrderNumber = 2;

        makeTokenServiceReturnUser();

        QuizModel quizModel = new QuizModel(quizId, "Quiz 1", new UserModel());
        QuestionModel questionModel = new QuestionModel();
        questionModel.setContent("Question 1");
        questionModel.setOrdNum(questionOrderNumber - 1);
        quizModel.addQuestion(questionModel);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizModel));

        assertThrows(QuestionNotFoundException.class, () -> quizService.removeQuestionFromQuiz(quizId, questionOrderNumber, token));

        verify(quizRepository, times(1)).findById(quizId);
        verifyNoMoreInteractions(questionRepository);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    public void testRemoveQuestionFromQuiz_withNonExistingQuizId_shouldThrowQuizNotFoundException() {
        long quizId = 1L;
        int questionOrderNumber = 1;

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.removeQuestionFromQuiz(quizId, questionOrderNumber, "valid_token"));

        verify(quizRepository, times(1)).findById(quizId);
        verifyNoMoreInteractions(questionRepository);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    public void testAddAnswerToQuestion_withValidQuizIdAndQuestionOrdinalNumberAndAnswerDtoAndToken_shouldReturnAddedAnswer() throws PermissionDeniedException, QuizNotFoundException, AnswersLimitException, QuestionNotFoundException {
        long quizId = 1L;
        int questionOrderNumber = 1;
        AnswerDto answerDto = new AnswerDto("Answer 1", 1);

        makeTokenServiceReturnUser();

        QuizModel quizModel = new QuizModel(quizId, "Quiz 1", new UserModel());
        QuestionModel questionModel = new QuestionModel();
        questionModel.setContent("Question 1");
        questionModel.setOrdNum(questionOrderNumber);
        questionModel.setId(1L);
        quizModel.addQuestion(questionModel);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizModel));
        when(answerRepository.save(any(AnswerModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnswerModel result = quizService.addAnswerToQuestion(quizId, questionOrderNumber, answerDto, token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Answer 1", result.getText());
        Assertions.assertEquals(1, result.getScore());
        Assertions.assertEquals(1, result.getOrdNum());

        verify(quizRepository, times(1)).findById(quizId);
        verify(answerRepository, times(1)).save(any(AnswerModel.class));
        verify(questionRepository, times(1)).save(questionModel);
    }

    @Test
    public void testAddAnswerToQuestion_withNonExistingQuizId_shouldThrowQuizNotFoundException() {
        long quizId = 1L;
        int questionOrderNumber = 1;
        AnswerDto answerDto = new AnswerDto("Answer 1", 1);

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.addAnswerToQuestion(quizId, questionOrderNumber, answerDto, "valid_token"));

        verify(quizRepository, times(1)).findById(quizId);
        verifyNoMoreInteractions(answerRepository);
        verifyNoMoreInteractions(questionRepository);
        verifyNoMoreInteractions(quizRepository);
    }


    @Test
    public void testRemoveAnswerFromQuestion_withValidQuizIdAndQuestionOrdinalNumberAndAnswerOrdinalNumberAndToken_shouldRemoveAnswer() throws QuizNotFoundException, PermissionDeniedException, QuestionNotFoundException, AnswerNotFoundException, AnswersLimitException, AnswersLimitException {
        long quizId = 1L;
        int questionOrderNumber = 1;
        int answerOrderNumber = 1;

        var user = makeTokenServiceReturnUser();

        QuizModel quizModel = new QuizModel(quizId, "Quiz 1", new UserModel());
        quizModel.setOwner(user);

        QuestionModel questionModel = new QuestionModel();
        questionModel.setContent("Question 1");
        questionModel.setOrdNum(questionOrderNumber);
        AnswerModel answerModel = new AnswerModel();
        answerModel.setId(1L);
        answerModel.setText("Answer 1");
        answerModel.setOrdNum(answerOrderNumber);
        questionModel.addAnswer(answerModel);
        quizModel.addQuestion(questionModel);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizModel));
        when(questionRepository.save(any(QuestionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        quizService.removeAnswerFromQuestion(quizId, questionOrderNumber, answerOrderNumber, token);

        verify(quizRepository, times(1)).findById(quizId);
        verify(answerRepository, times(1)).delete(answerModel);
        verify(questionRepository, times(1)).save(questionModel);
    }

    @Test
    public void testRemoveAnswerFromQuestion_withNonExistingQuizId_shouldThrowQuizNotFoundException() {
        long quizId = 1L;
        int questionOrderNumber = 1;
        int answerOrderNumber = 1;

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> quizService.removeAnswerFromQuestion(quizId, questionOrderNumber, answerOrderNumber, "valid_token"));

        verify(quizRepository, times(1)).findById(quizId);
        verifyNoMoreInteractions(answerRepository);
        verifyNoMoreInteractions(questionRepository);
        verifyNoMoreInteractions(quizRepository);
    }

    private UserModel makeTokenServiceReturnUser(){
        long userId = 1;
        var user = new UserModel();
        user.setId(userId);

        when(tokenService.getUserFromToken(token)).thenReturn(user);

        return user;

    }


}
