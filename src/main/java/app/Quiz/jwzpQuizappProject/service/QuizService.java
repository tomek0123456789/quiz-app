package app.Quiz.jwzpQuizappProject.service;

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
import app.Quiz.jwzpQuizappProject.models.questions.QuestionStatus;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizPatchDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizStatus;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;

@Service
public class QuizService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final CategoryService categoryService;
    private final TokenService tokenService;
    private final Clock clock;
    private final int QUESTIONS_LIMIT = 50;
    private final int VALID_QUESTION_LIMIT = 2;
    private final int ANSWERS_LIMIT = 4;

    public QuizService(AnswerRepository answerRepository, QuestionRepository questionRepository, QuizRepository quizRepository, CategoryService categoryService, TokenService tokenService, Clock clock) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.categoryService = categoryService;
        this.tokenService = tokenService;
        this.clock = clock;
    }
    private boolean checkQuizOwnership(Long userId, long quizOwnerId) {
        return userId == quizOwnerId;
    }

    private boolean validateUserQuizAuthorities(UserModel user, Long quizOwnerId) {
        return user.isAdmin() || checkQuizOwnership(quizOwnerId, user.getId());
    }

    private void throwPermissionDeniedException(long quizId) throws PermissionDeniedException {
        throw new PermissionDeniedException("You are neither an admin, nor an owner of a quiz with id: " + quizId + ".");
    }

    private QuizNotFoundException getPreparedQuizNotFoundException(long quizId) {
        return new QuizNotFoundException("Quiz with id: " + quizId + " was not found.");
    }

    // this method does two things: validates and returns a quiz
    // because there's no reason to query db twice just to get a quiz again in any other method
    private QuizModel validateUserAgainstQuiz(String token, long quizId) throws QuizNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
        if (!validateUserQuizAuthorities(user, quiz.getId())) {
            throwPermissionDeniedException(quiz.getId());
        }
        return quiz;
    }

    ////////////////////////

    public QuizModel getSingleQuiz(Long quizId) throws QuizNotFoundException {
        return quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
    }
    public List<QuizModel> getMultipleQuizzes(
            // todo start from here
            Optional<String> titlePart,
            Optional<String> categoryName,
            Optional<Boolean> validQuizzes
    ) {
        String predicate = String.valueOf(titlePart.isPresent() ? 1 : 0) + (categoryName.isPresent() ? 1 : 0) + (validQuizzes.isPresent() ? 1 : 0);

        return switch (predicate) {
            case "001" -> quizRepository.findAllByQuizStatus(QuizStatus.VALID);
            case "010" -> quizRepository.findAllByCategoryName(categoryName.get());
            case "011" -> quizRepository.findAllByCategoryNameAndQuizStatus(categoryName.get(), QuizStatus.VALID);
            case "100" -> quizRepository.findAllByTitleContaining(titlePart.get());
            case "101" -> quizRepository.findAllByTitleContainingAndQuizStatus(titlePart.get(), QuizStatus.VALID);
            case "110" -> quizRepository.findAllByTitleContainingAndCategoryName(titlePart.get(), categoryName.get());
            case "111" -> quizRepository.findAllByTitleContainingAndCategoryNameAndQuizStatus(titlePart.get(), categoryName.get(), QuizStatus.VALID);
            default -> quizRepository.findAll();
        };
    }
    public List<QuizModel> getUserQuizzes(String token){
        var user = tokenService.getUserFromToken(token);
        return quizRepository.findAllByOwner(user);
    }
    public QuizModel addQuiz(QuizDto quizDto, String token) throws CategoryNotFoundException {
        UserModel user = tokenService.getUserFromToken(token);
        var category = categoryService.getSingleCategory(quizDto.categoryId());
        QuizModel quizModel = new QuizModel(quizDto.name(), quizDto.description(), user, category, clock.instant());
        quizRepository.save(quizModel);
        return quizModel;
    }

    public QuizModel updateQuiz(long quizId, QuizPatchDto quizPatchDto, String token) throws QuizNotFoundException, PermissionDeniedException, CategoryNotFoundException {
        var quiz = validateUserAgainstQuiz(token, quizId);
        if (quizPatchDto.title() != null) {
            quiz.setTitle(quizPatchDto.title());
        }
        if (quizPatchDto.description() != null) {
            quiz.setDescription(quizPatchDto.description());
        }
        if (quizPatchDto.categoryId() != null) {
            quiz.setCategory(categoryService.getSingleCategory(quizPatchDto.categoryId()));
        }
        //todo maybe reflection? doesn't scale well in case more fields were added
        quizRepository.save(quiz);
        return quiz;
    }

    public QuizModel updateQuiz(QuizModel quiz) throws CategoryNotFoundException {
        // validate if category exists
        categoryService.getSingleCategory(quiz.getCategory().getId());
        quiz.setQuestions(Collections.emptyList());
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(long quizId, String token) throws PermissionDeniedException, QuizNotFoundException {
        var quizToDelete = validateUserAgainstQuiz(token, quizId);
        quizRepository.delete(quizToDelete);
    }

    // questions

    public QuestionModel addQuestionToQuiz(long quizId, QuestionDto questionDto, String token) throws PermissionDeniedException, QuestionsLimitException, QuizNotFoundException {
        var quiz = validateUserAgainstQuiz(token, quizId);
        var question = new QuestionModel(quiz.nextQuestionOrdinalNumber(), questionDto.content(), clock.instant(), quiz.getId());
        if (quiz.questionsSize() >= QUESTIONS_LIMIT) {
            throw new QuestionsLimitException("A quiz cannot have more than 50 questions");
        }
        questionRepository.save(question);
        quiz.addQuestion(question);
        quizRepository.save(quiz);
        return question;
    }

    public void removeQuestionFromQuiz(long quizId, int questionOrdinalNumber, String token) throws PermissionDeniedException, QuestionsLimitException, QuizNotFoundException, QuestionNotFoundException {
        var quiz = validateUserAgainstQuiz(token, quizId);
        var quizQuestionsSize = quiz.questionsSize();
        // todo is it necessary since quiz.removeQuestion throws when it doesn't find a valid quiz?
        //  the message is more informative than just not found
        //  if so, add to removeAnswerFromQuestion
        if (questionOrdinalNumber > quizQuestionsSize) {
            throw new QuestionsLimitException("You tried to delete question no. " + questionOrdinalNumber + ", but this quiz has only " + quizQuestionsSize + " questions.");
        }
        QuestionModel question;
        try {
            question = quiz.removeQuestion(questionOrdinalNumber);
        } catch (NoSuchElementException e) {
            throw new QuestionNotFoundException("Question with ordinal number: " + questionOrdinalNumber + "  was not found in quiz with id: " + quizId + ".");
        }
        questionRepository.delete(question);
    }

    // answers

    public AnswerModel addAnswerToQuestion(long quizId, int questionOrdinalNumber, AnswerDto answerDto, String token) throws PermissionDeniedException, QuizNotFoundException, AnswersLimitException, QuestionNotFoundException {
        var quiz = validateUserAgainstQuiz(token, quizId);
        QuestionModel question;
        try {
            question = quiz.getSingleQuestionByOrdNum(questionOrdinalNumber);
        } catch (QuestionNotFoundException e) {
            // stinky, can you change exception message without throwing another exception?
            // i dont want to pass unnecessary arguments to that function especially it is used elsewhere
            throw new QuestionNotFoundException("Question with ordinal number: " + questionOrdinalNumber + "  was not found in quiz with id: " + quizId + ".");
        }
        if (question.answersSize() >= ANSWERS_LIMIT) {
            throw new AnswersLimitException("A question cannot have more than 4 answers.");
        }

        var answer = new AnswerModel(question.nextAnswerOrdinalNumber(), answerDto.text(), answerDto.score(), clock.instant(), question.getId());
        answerRepository.save(answer);
        question.addAnswer(answer);
        if (question.answersSize() >= VALID_QUESTION_LIMIT) {
            question.setQuestionStatus(QuestionStatus.VALID);
        }
        questionRepository.save(question);
        return answer;
    }

    public void removeAnswerFromQuestion(long quizId, int questionOrdinalNumber, int answerOrdinalNumber, String token) throws QuizNotFoundException, PermissionDeniedException, QuestionNotFoundException, AnswerNotFoundException, AnswersLimitException {
        var quiz = validateUserAgainstQuiz(token, quizId);
        QuestionModel question;
        AnswerModel answer;
        try {
            question = quiz.getSingleQuestionByOrdNum(questionOrdinalNumber);
            answer = question.getSingleAnswerByOrdNum(answerOrdinalNumber);
        } catch (QuestionNotFoundException e) {
            throw new QuestionNotFoundException("Question with ordinal number: " + questionOrdinalNumber + " was not found in quiz with id: " + quizId + ".");
        } catch (AnswerNotFoundException e) {
            throw new AnswerNotFoundException("Answer with ordinal number: " + answerOrdinalNumber + " in question with ordinal number: " + questionOrdinalNumber + " in quiz with id: " + quizId + " was not found.");
        }
        question.removeAnswer(answer);
        if (question.answersSize() < VALID_QUESTION_LIMIT) {
            question.setQuestionStatus(QuestionStatus.INVALID);
        }
        answerRepository.delete(answer);
        questionRepository.save(question);
    }
}
