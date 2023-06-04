package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.RoomAuthoritiesValidator;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerAlreadyExists;
import app.Quiz.jwzpQuizappProject.exceptions.answers.AnswerNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.auth.PermissionDeniedException;
import app.Quiz.jwzpQuizappProject.exceptions.questions.QuestionNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.quizzes.QuizNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.results.ResultNotFoundException;
import app.Quiz.jwzpQuizappProject.exceptions.rooms.RoomNotFoundException;
import app.Quiz.jwzpQuizappProject.models.answers.AnswerModel;
import app.Quiz.jwzpQuizappProject.models.questions.QuestionModel;
import app.Quiz.jwzpQuizappProject.models.quizzes.QuizModel;
import app.Quiz.jwzpQuizappProject.models.results.QuestionAndUsersAnswerModel;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsDto;
import app.Quiz.jwzpQuizappProject.models.results.ResultsModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultsService {

    final QuestionRepository questionRepository;
    final QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository;
    final QuizResultsRepository quizResultsRepository;
    final ResultsRepository resultsRepository;
    final QuizRepository quizRepository;
    final AnswerRepository answerRepository;
    final TokenService tokenService;
    final RoomAuthoritiesValidator roomAuthoritiesValidator;
    final RoomRepository roomRepository;

    public ResultsService(QuestionRepository questionRepository, QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository, QuizResultsRepository quizResultsRepository, ResultsRepository resultsRepository, QuizRepository quizRepository, AnswerRepository answerRepository, TokenService tokenService, RoomAuthoritiesValidator roomAuthoritiesValidator, RoomRepository roomRepository) {
        this.questionRepository = questionRepository;
        this.questionAndUsersAnswerRepository = questionAndUsersAnswerRepository;
        this.quizResultsRepository = quizResultsRepository;
        this.resultsRepository = resultsRepository;
        this.quizRepository = quizRepository;
        this.answerRepository = answerRepository;
        this.tokenService = tokenService;
        this.roomAuthoritiesValidator = roomAuthoritiesValidator;
        this.roomRepository = roomRepository;
    }

    private boolean validateUserInfoResultAuthorities(UserModel user, ResultsModel resultsModel){
        return user.isAdmin() || resultsModel.getOwner() == user;
    }

    public Set<QuizResultsModel> getMyResultsForQuiz(String token, long id) {
        var user = tokenService.getUserFromToken(token);

        List<ResultsModel> results = this.resultsRepository.findAll().stream().filter(result -> result.getOwner() == user).toList();

        return results.stream()
                .flatMap(result -> result.getQuizzesResults().stream())
                .filter(quizResult -> quizResult.getQuiz().getId() == id)
                .collect(Collectors.toSet());
    }

    public QuizResultsModel getMyBestResultForQuiz(String token, long quizId){
        return Collections.max(getMyResultsForQuiz(token, quizId), Comparator.comparingLong(QuizResultsModel::getScore));
    }

    public List<ResultsModel> getAllMyResults(String token){
        var user = tokenService.getUserFromToken(token);
        return this.resultsRepository.findAll().stream().filter(resultsModel -> resultsModel.getOwner()!=null && Objects.equals(resultsModel.getOwner().getId(), user.getId())).toList();
    }
    

    public List<ResultsModel> getResultsForRoom(String token,long roomId) throws PermissionDeniedException, RoomNotFoundException {
        var user = tokenService.getUserFromToken(token);
        var room = roomRepository.findById(roomId);

        if(room.isEmpty()){
            throw new RoomNotFoundException("Room with id=" + roomId + " not found");
        }

        if(!this.roomAuthoritiesValidator.validateUserRoomInfoAuthorities(user, room.get())){
            throw new PermissionDeniedException(user.getName() + " is not authorized to get results for room " + room.get().getRoomName() + " with id=" + room.get().getId());
        }

        return this.resultsRepository.findAll().stream()
                .filter(resultsModel -> resultsModel.getRoom()!=null && Objects.equals(resultsModel.getRoom().getId(), roomId)).toList();
    }

    public ResultsModel getSingleResult(long id, String token) throws ResultNotFoundException, PermissionDeniedException {
        var user = tokenService.getUserFromToken(token);
        var result = this.resultsRepository.findById(id);

        if(result.isEmpty()){
            throw new ResultNotFoundException("There is no results with ID: " + id);
        }

        if(!validateUserInfoResultAuthorities(user,result.get())){
           throw new PermissionDeniedException("Not valid user");
        }

        return result.get();
    }


    public ResultsModel createResults(ResultsDto newResults, String token) throws QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists, AnswerNotFoundException {
        long roomScore = 0;
        var user = tokenService.getUserFromToken(token);

        ResultsModel resultsModel = new ResultsModel();
        resultsModel.setOwner(user);

        System.out.println(newResults.quizzesResults());

        for (QuizResultsModel quizResult : newResults.quizzesResults()) {
            Optional<QuizModel> quiz = this.quizRepository.findById(quizResult.getQuizId());

            if (quiz.isEmpty()) {
                throw new QuizNotFoundException("quiz is empty");
            }

            quizResult.setQuiz(quiz.get());

            Set<QuestionAndUsersAnswerModel> qaaSet = new HashSet<>();

            long quizScore = 0;
            for (QuestionAndUsersAnswerModel qaa : quizResult.getQuestionsAndAnswers()) {
                int questionOrdNum = (int) qaa.getQuestionOrdNum();

                if (questionOrdNum >= quiz.get().getQuestions().size() || questionOrdNum <0) {
                    throw new QuestionNotFoundException("Question ord num out of bounds or not provided: " + questionOrdNum + " " + quizResult.getQuizId());
                }

                Optional<QuestionModel> question = Optional.ofNullable(quiz.get().getSingleQuestionByOrdNum(questionOrdNum));


                if (question.isEmpty()) {
                    throw new QuestionNotFoundException("Invalid question ID " + questionOrdNum);
                }

                boolean alreadyContainsQuestion = qaaSet.stream()
                        .anyMatch(tempQaa -> tempQaa.getQuestionOrdNum() == questionOrdNum);

                if(alreadyContainsQuestion){
                    throw new AnswerAlreadyExists("Repeated question!");
                }

                int ansOrdNum = (int) qaa.getUserAnswerOrdNum();

                System.out.println( question.get().getId());

                if (ansOrdNum >= question.get().getAnswers().size() || ansOrdNum <0) {
                    throw new AnswerNotFoundException("Answer ord num out of bounds or not provided " + ansOrdNum + " " + questionOrdNum + " " + quizResult.getQuizId());
                }

                Optional<AnswerModel> answer = Optional.ofNullable(question.get().getSingleAnswerByOrdNum(ansOrdNum));

                if (answer.isEmpty() || ansOrdNum >= question.get().getAnswers().size() || ansOrdNum <0) {
                    throw new AnswerNotFoundException("Invalid answer ord num");
                }

                qaa.setQuestion(question.get());
                qaa.setAnswer(answer.get());

                quizScore += answer.get().getScore();

                this.questionAndUsersAnswerRepository.save(qaa);

                qaaSet.add(qaa);
            }
            quizResult.setScore(quizScore);
            roomScore += quizScore;
//            results.add(quizResult);
            this.quizResultsRepository.save(quizResult);
        }
        resultsModel.setQuizzesResults(newResults.quizzesResults());
        resultsModel.setScore(roomScore);
        this.resultsRepository.save(resultsModel);

        return resultsModel;
    }




    public ResultsModel createResultsForRoom(ResultsDto newResults,long roomId, String token) throws RoomNotFoundException, AnswerNotFoundException, QuestionNotFoundException, QuizNotFoundException, AnswerAlreadyExists {
        var resultsModel = createResults(newResults, token);
        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("no room with ID:" + roomId));

        resultsModel.setRoom(room);
        this.resultsRepository.save(resultsModel);

        return resultsModel;
    }

    public void deleteResult(ResultsModel result){
        this.resultsRepository.delete(result);

    }


}
