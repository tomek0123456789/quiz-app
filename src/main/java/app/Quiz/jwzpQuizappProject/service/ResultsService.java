package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.RoomAuthoritiesValidator;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.TimeExceededException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.*;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultsService {

    private final QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    private final QuizResultsRepository quizResultsRepository;
    private final ResultsRepository resultsRepository;
    private final QuizRepository quizRepository;
    private final TokenService tokenService;
    private final RoomAuthoritiesValidator roomAuthoritiesValidator;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public ResultsService(QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository, QuizResultsRepository quizResultsRepository, ResultsRepository resultsRepository, QuizRepository quizRepository, TokenService tokenService, RoomAuthoritiesValidator roomAuthoritiesValidator, RoomRepository roomRepository, UserRepository userRepository, Clock clock) {
        this.questionAndUsersAnswerRepository = questionAndUsersAnswerRepository;
        this.quizResultsRepository = quizResultsRepository;
        this.resultsRepository = resultsRepository;
        this.quizRepository = quizRepository;
        this.tokenService = tokenService;
        this.roomAuthoritiesValidator = roomAuthoritiesValidator;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    private QuizResultsModel getQuizResultsWithId(long id) throws AnswerNotFoundException {
        return quizResultsRepository.findById(id).orElseThrow(() -> new AnswerNotFoundException("Quiz results with id: " + id + " were not found."));
    }

    private ResultsModel getResultsWithId(long id) throws ResultNotFoundException {
        return resultsRepository.findById(id).orElseThrow(() -> new ResultNotFoundException("Results with id: " + id + " were not found."));
    }

    private QuestionAndUsersAnswerModel getQaaById(long id) throws AnswerNotFoundException {
        return questionAndUsersAnswerRepository.findById(id).orElseThrow(() -> new AnswerNotFoundException("no QuestionAndUsersAnswerModel with ID:" + id));
    }

    private boolean validateUserInfoResultAuthorities(UserModel user, ResultsModel resultsModel) {
        return user.isAdmin() || resultsModel.getOwner() == user;
    }

    public Set<QuizResultsModel> getMyResultsForQuiz(long id, String token) {
        var user = tokenService.getUserFromToken(token);

        List<ResultsModel> results = resultsRepository.findAllByOwner(user);
        return results.stream()
                .flatMap(result -> result.getQuizzesResults().stream())
                .filter(quizResult -> quizResult.getQuiz().getId() == id)
                .collect(Collectors.toSet());
    }

    public QuizResultsModel getMyBestResultForQuiz(String token, long quizId) {
        return Collections.max(getMyResultsForQuiz(quizId, token), Comparator.comparingLong(QuizResultsModel::getScore));
    }

    public List<ResultsModel> getAllMyResults(String token) {
        var user = tokenService.getUserFromToken(token);
        return resultsRepository.findAllByOwner(user);
    }

    public List<ResultsModel> getResultsForRoom(long roomId, String token) throws PermissionDeniedException, RoomNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room with id: " + roomId + " was not found."));
        if (!roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room)) {
            throw new PermissionDeniedException(user.getName() + " is not authorized to get results for room: " + room.getRoomName() + " with id: " + room.getId() + ".");
        }
        return resultsRepository.findByRoomId(roomId);
    }

    public ResultsModel getSingleResult(long id, String token) throws ResultNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var result = resultsRepository.findById(id).orElseThrow(() -> new ResultNotFoundException("Results with id: " + id + " were not found."));
        if (!validateUserInfoResultAuthorities(user, result)) {
            throw new PermissionDeniedException("User with id: " + user.getId() + " does not have authority to read results with id: " + result.getId());
        }
        return result;
    }

    private QuestionModel validateQuestionOrderNumberAndGetQuestion(
            int questionOrdNum,
            QuizModel quiz,
            Set<QuestionAndUsersAnswerModel> qaaSet
    ) throws QuestionNotFoundException, AnswerAlreadyExists {
        if (questionOrdNum > quiz.getQuestions().size() || questionOrdNum <= 0) {
            throw new QuestionNotFoundException("Question order number: " + questionOrdNum + " for quiz with id " + quiz.getId() + " is out of bounds or was not provided.");
        }
        QuestionModel question;
        try {
            question = quiz.getSingleQuestionByOrdNum(questionOrdNum);
        } catch (QuestionNotFoundException e) {
            throw new QuestionNotFoundException("Question with order number: " + questionOrdNum + " for quiz with id: " + quiz.getId() + " was not found.");
        }

        boolean alreadyContainsQuestion = qaaSet.stream()
                .anyMatch(tempQaa -> tempQaa.getQuestionOrdNum() == questionOrdNum);
        if (alreadyContainsQuestion) {
            throw new AnswerAlreadyExists("Question with order number: " + questionOrdNum + " has been repeated.");
        }

        return question;
    }

    private AnswerModel validateOrdNumAndGetAnswer(int ansOrdNum, QuestionModel question) throws AnswerNotFoundException {
        if (ansOrdNum > question.getAnswers().size() || ansOrdNum <= 0) {
            throw new AnswerNotFoundException("Answer order number: " + ansOrdNum + " for question with order number: " + question.getOrdNum() + " is out of bounds or was not provided.");
        }
        AnswerModel answer;
        try {
            answer = question.getSingleAnswerByOrdNum(ansOrdNum);
        } catch (AnswerNotFoundException e) {
            throw new AnswerNotFoundException("Answer with order number: " + ansOrdNum + " for question with order number: " + question.getOrdNum() + " was not found.");
        }
        return answer;
    }


    public ResultsModel createResults(ResultsDto newResults, String token) throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        var user = tokenService.getUserFromToken(token);
        ResultsModel resultsModel = new ResultsModel(clock.instant(), user);
        Set<QuizResultsModel> quizResults = newResults.quizzesResults();
        long roomScore = 0;
        for (QuizResultsModel quizResult : quizResults) {
            QuizModel quiz = quizRepository.findById(quizResult.getQuizId()).orElseThrow(() -> new QuizNotFoundException("Quiz with id: " + quizResult.getQuizId() + " was not found."));
            quizResult.setQuiz(quiz);

            Set<QuestionAndUsersAnswerModel> qaaSet = new HashSet<>();
            long quizScore = 0;
            for (QuestionAndUsersAnswerModel qaa : quizResult.getQuestionsAndAnswers()) {
                int questionOrdNum = qaa.getQuestionOrdNum();
                var question = validateQuestionOrderNumberAndGetQuestion(questionOrdNum, quiz, qaaSet);
                int ansOrdNum = qaa.getUserAnswerOrdNum();

                if (ansOrdNum > question.getAnswers().size() || ansOrdNum <= 0) {
                    throw new AnswerNotFoundException("Answer ord num out of bounds or not provided " + ansOrdNum + " " + questionOrdNum + " " + quizResult.getQuizId());
                }

                AnswerModel answer = validateOrdNumAndGetAnswer(ansOrdNum, question);
                qaa.setQuestion(question);
                qaa.setAnswer(answer);
                quizScore += answer.getScore();
                questionAndUsersAnswerRepository.save(qaa);
                qaaSet.add(qaa);
            }
            quizResult.setScore(quizScore);
            roomScore += quizScore;
            quizResultsRepository.save(quizResult);
        }
        resultsModel.setQuizzesResults(newResults.quizzesResults());
        resultsModel.setScore(roomScore);
        resultsRepository.save(resultsModel);

        return resultsModel;
    }

    public ResultsModel createResultsForRoom(ResultsDto newResults, long roomId, String token)
            throws RoomNotFoundException, AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, TimeExceededException {
        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room with id: " + roomId + " was not found."));
        if (clock.instant().isAfter(room.getEndTime())) {
            throw new TimeExceededException("Time for sending results for room with id: " + roomId + " has already passed.");
        }
        var resultsModel = createResults(newResults, token);
        resultsModel.setRoom(room);
        resultsRepository.save(resultsModel);
        return resultsModel;
    }

    public void deleteSingleResult(ResultsModel result) {
        resultsRepository.delete(result);
    }

    public void deleteAllResults(List<ResultsModel> results) {
        resultsRepository.deleteAll(results);
    }

    public QuestionAndUsersAnswerModel updateQuestionAndUsersAnswer(QuestionAndUsersAnswerPatchDto questionAndUsersAnswerPatchDto) throws AnswerNotFoundException, QuizNotFoundException, QuestionNotFoundException {
        var originalQuestionAndUsersAnswer = getQaaById(questionAndUsersAnswerPatchDto.id());
        var quiz = quizRepository.findById(questionAndUsersAnswerPatchDto.quizId()).orElseThrow(() -> new QuizNotFoundException("Quiz with id: " + questionAndUsersAnswerPatchDto.quizId() + " was not found."));
        originalQuestionAndUsersAnswer.update(questionAndUsersAnswerPatchDto, quiz);
        questionAndUsersAnswerRepository.save(originalQuestionAndUsersAnswer);
        return originalQuestionAndUsersAnswer;
    }

    public QuizResultsModel updateQuizResults(QuizResultsPatchDto quizResultsPatchDto) throws AnswerNotFoundException, QuizNotFoundException {
        var originalQuizResults = getQuizResultsWithId(quizResultsPatchDto.quizResultsId());
        var quiz = quizResultsPatchDto.quizId() != null ?
                quizRepository.findById(quizResultsPatchDto.quizId()).orElseThrow(() -> new QuizNotFoundException("Quiz with id: " + quizResultsPatchDto.quizId() + " was not found."))
                : null;
        originalQuizResults.update(quizResultsPatchDto, quiz);
        quizResultsRepository.save(originalQuizResults);
        return originalQuizResults;
    }

    public ResultsModel updateResults(ResultsPatchDto resultsPatchDto) throws ResultNotFoundException, RoomNotFoundException {
        var originalResults = getResultsWithId(resultsPatchDto.resultsId());
        var room = resultsPatchDto.roomId() != null ?
                roomRepository.findById(resultsPatchDto.roomId()).orElseThrow(() -> new RoomNotFoundException("Room with id: " + resultsPatchDto.roomId() + " was not found."))
                : null;
        var owner = resultsPatchDto.ownerId() != null ?
                userRepository.findById(resultsPatchDto.ownerId()).orElseThrow(() -> new UsernameNotFoundException("User with id: " + resultsPatchDto.roomId() + " was not found."))
                : null;

        originalResults.update(resultsPatchDto, owner, room);
        resultsRepository.save(originalResults);
        return originalResults;
    }

    public void deleteQuestionAndAnswer(long qaaId, long quizResultsId) throws AnswerNotFoundException {
        var qaa = getQaaById(qaaId);
        var quizResults = getQuizResultsWithId(quizResultsId);

        quizResults.deleteQuestionsAndAnswers(qaa);
        questionAndUsersAnswerRepository.delete(qaa);

        quizResultsRepository.save(quizResults);
    }

    public void deleteQuizResults(long quizResultsId, long resultsId) throws AnswerNotFoundException, ResultNotFoundException {
        var quizResults = getQuizResultsWithId(quizResultsId);
        // qaas are deleted on cascade
        var results = getResultsWithId(resultsId);
        results.removeQuizResults(quizResults);
        resultsRepository.save(results);
        quizResults.setQuiz(null);
        quizResultsRepository.delete(quizResults);
    }

    public void deleteResults(long resultsId) throws ResultNotFoundException {
        var results = getResultsWithId(resultsId);
        results.setOwner(null);
        results.setRoom(null);

        resultsRepository.delete(results);
    }

    @Transactional
    public List<UserRoomResultDto> getLeaderboardForRoom(long roomId, String token) throws RoomNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room with id: " + roomId + " was not found."));

        if (!roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room)) {
            throw new PermissionDeniedException(user.getName() + " is not authorized to get leaderboard for room: " + room.getRoomName() + ".");
        }

        List<ResultsModel> allResults = resultsRepository.findByRoomId(roomId);

        Map<UserModel, List<ResultsModel>> resultsByUser = allResults.stream()
                .collect(Collectors.groupingBy(ResultsModel::getOwner));

        List<UserRoomResultDto> leaderboard = new ArrayList<>();

        for (var entry : resultsByUser.entrySet()) {
            UserModel participant = entry.getKey();
            List<ResultsModel> userResults = entry.getValue();

            long totalScore = userResults.stream()
                    .flatMap(r -> r.getQuizzesResults().stream())
                    .collect(Collectors.groupingBy(
                            qr -> qr.getQuiz().getId(),
                            Collectors.maxBy(Comparator.comparingLong(QuizResultsModel::getScore))
                    ))
                    .values().stream()
                    .filter(Optional::isPresent)
                    .mapToLong(opt -> opt.get().getScore())
                    .sum();

            leaderboard.add(new UserRoomResultDto(participant.getId(), participant.getName(), totalScore));
        }

        leaderboard.sort((a, b) -> Long.compare(b.totalScore(), a.totalScore()));

        return leaderboard;
    }
}
