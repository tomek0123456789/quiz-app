package app.Quiz.jwzpQuizappProject.models.results;

import app.Quiz.jwzpQuizappProject.models.users.UserModel;

import java.time.LocalDateTime;
import java.util.Set;

public record ResultsDto(
    Set<QuizResultsModel> quizzesResults,

    UserModel owner,

    LocalDateTime createdAt,

    long score

) {}
