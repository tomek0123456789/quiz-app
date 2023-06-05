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
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizDto;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class QuizService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final CategoryService categoryService;
    private final TimeService timeService;
    private final TokenService tokenService;

    private final int QUESTIONS_LIMIT = 50;
    private final int ANSWERS_LIMIT = 4;

    public QuizService(AnswerRepository answerRepository, QuestionRepository questionRepository, QuizRepository quizRepository, CategoryService categoryService, TimeService timeService, TokenService tokenService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.categoryService = categoryService;
        this.timeService = timeService;
        this.tokenService = tokenService;
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

    ////////////////////////

    public QuizModel getSingleQuiz(Long quizId) throws QuizNotFoundException {
        return quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
    }

    public List<QuizModel> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<QuizModel> getQuizzesWithTitleContaining(String titlePart) {
        return quizRepository.findAllByTitleContaining(titlePart);
    }

    public List<QuizModel> getQuizzesByTitleOrCategory(Optional<String> titlePart, Optional<String> categoryName) {
        List<QuizModel> quizzes;
        if (titlePart.isPresent()) {
            if (categoryName.isPresent()) {
                quizzes = quizRepository.findAllByTitleContainingAndCategoryName(titlePart.get(), categoryName.get());
            } else {
                quizzes = quizRepository.findAllByTitleContaining(titlePart.get());
            }
        } else {
            if (categoryName.isPresent()) {
                quizzes = quizRepository.findAllByCategoryName(categoryName.get());
            } else {
                quizzes = quizRepository.findAll();
            }
        }
        return quizzes;
    }
    public QuizModel addQuiz(QuizDto quizDto, String token) throws CategoryNotFoundException {
        UserModel user = tokenService.getUserFromToken(token);
        var category = categoryService.getSingleCategory(quizDto.categoryId());
        // TODO: validate is category exists (do it here or somwhere else?
        QuizModel quizModel = new QuizModel(user, quizDto.name(), quizDto.description(), category, timeService.getCurrentTime());
        quizRepository.save(quizModel);
        return quizModel;
    }

//    // todo finish that
//    public QuizModel updateQuiz(QuizDto newQuiz) {
//        var quiz = quizRepository.findByName()
//    }

    public void deleteQuiz(long quizId, String token) throws PermissionDeniedException, QuizNotFoundException {
        // todo validate if user sending the request is the actual user or an admin
        var user = tokenService.getUserFromToken(token);
        var quizToDelete = quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
        if (!validateUserQuizAuthorities(user, quizToDelete.getOwnerId())) {
            throwPermissionDeniedException(quizToDelete.getId());
        }
        quizRepository.delete(quizToDelete);
        // todo remove questions and answers from the quiz
    }

    // questions

    // maybe in QuestionService?
    public QuestionModel addQuestionToQuiz(long quizId, QuestionDto questionDto, String token) throws PermissionDeniedException, QuestionsLimitException, QuizNotFoundException {
        // implement question limit to a quiz
        //
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
        var question = new QuestionModel(questionDto.content(), quiz.getQuestionsSize(), quiz.getId(), timeService.getCurrentTime());
        if (!validateUserQuizAuthorities(user, quiz.getOwnerId())) {
            throwPermissionDeniedException(quiz.getId());
        }
        // == or >=?
        if (quiz.getQuestionsSize() >= QUESTIONS_LIMIT) {
            throw new QuestionsLimitException("A quiz cannot have more than 50 questions");
        }
        //todo add question duplicate check
        questionRepository.save(question);
        //todo remove that with update
        quiz.addQuestion(question);
        quizRepository.save(quiz);
        return question;
        // is it automatically added because of the annotation?
    }

    public void removeQuestionFromQuiz(long quizId, int questionOrdinalNumber, String token) throws PermissionDeniedException, QuestionsLimitException, QuizNotFoundException, QuestionNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
        if (!validateUserQuizAuthorities(user, quiz.getOwnerId())) {
            throwPermissionDeniedException(quiz.getId());
        }
        var quizQuestionsSize = quiz.getQuestionsSize();
        // is it necessary since quiz.removeQuestion throws when it doesn't find a valid quiz?
        // the message is more informative than just not found
        // if so, add to removeAnswerFromQuestion
        if (questionOrdinalNumber > quizQuestionsSize) {
            throw new QuestionsLimitException("You tried to delete question no. " + questionOrdinalNumber + ", but this quiz has only " + quizQuestionsSize + " questions.");
        }
        QuestionModel question;
        try {
            question = quiz.removeQuestion(questionOrdinalNumber);
        } catch (NoSuchElementException e) {
            throw new QuestionNotFoundException("Question with ordinal number: " + questionOrdinalNumber + "  was not found in quiz with id: " + quizId + ".");
        }
        // does quiz need to be saved after deleting a question?
        // does order of deletions matter here, will it crash?
        questionRepository.delete(question); // or questionService.deleteQuestion(question) | questionService.deleteQuestionWithId(questionId.getId())
//        TODO uncomment line below after implementing answerService
//        answerService.removeQuizAnswers(question.getAnswers());
        answerRepository.deleteAll(question.getAnswers());
    }

    // answers

    public AnswerModel addAnswerToQuestion(long quizId, int questionOrdinalNumber, AnswerDto answerDto, String token) throws PermissionDeniedException, QuizNotFoundException, AnswersLimitException, QuestionNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
        if (!validateUserQuizAuthorities(user, quiz.getOwnerId())) {
            throwPermissionDeniedException(quiz.getId());
        }
        QuestionModel question;
        try {
            question = quiz.removeQuestion(questionOrdinalNumber);
        } catch (QuestionNotFoundException e) {
            // stinky, can you change exception message without throwing another exception?
            throw new QuestionNotFoundException("Question with ordinal number: " + questionOrdinalNumber + "  was not found in quiz with id: " + quizId + ".");
        }
        // == or >=?
        if (question.getAnswersSize() >= ANSWERS_LIMIT) {
            throw new AnswersLimitException("A question cannot have more than 4 answers.");
        }
        var answer = new AnswerModel(answerDto.text(), answerDto.score(), question.getAnswersSize(), timeService.getCurrentTime());
        // does quiz need to be saved after deleting a question?    // u mean adding a question?
        answerRepository.save(answer);
        question.addAnswer(answer);
        questionRepository.save(question);      // without it it doesnt work
        return answer;
    }

    public void removeAnswerFromQuestion(long quizId, int questionOrdinalNumber, int answerOrdinalNumber, String token) throws QuizNotFoundException, PermissionDeniedException, QuestionNotFoundException, AnswerNotFoundException, AnswersLimitException {
        var user = tokenService.getUserFromToken(token);
        var quiz = quizRepository.findById(quizId).orElseThrow(() -> getPreparedQuizNotFoundException(quizId));
        if (!validateUserQuizAuthorities(user, quiz.getOwnerId())) {
            throwPermissionDeniedException(quiz.getId());
        }
        QuestionModel question;
        AnswerModel answer;
        try {
            question = quiz.getSingleQuestionByOrdNum(questionOrdinalNumber);
            answer = question.getSingleAnswerByOrdNum(answerOrdinalNumber);
        } catch (QuestionNotFoundException e) {
            throw new QuestionNotFoundException("Question with ordinal number: " + questionOrdinalNumber + "  was not found in quiz with id: " + quizId + ".");
        } catch (AnswerNotFoundException e) {
            throw new AnswerNotFoundException("Answer with ordinal number: " + answerOrdinalNumber + " in question with ordinal number: " + questionOrdinalNumber + " in quiz with id: " + quizId + " was not found.");
        }

        if(question.getAnswersSize() <=2){
            throw new AnswersLimitException("A question cannot have less than 2 answers.");
        }

        question.removeAnswer(answer);
        // does quiz need to be saved after deleting a question?
        answerRepository.delete(answer);
    }

    public List<QuizModel> getUserQuizzes(String token){
        var user = tokenService.getUserFromToken(token);
        return quizRepository.findAllByOwner(user);
    }

}
