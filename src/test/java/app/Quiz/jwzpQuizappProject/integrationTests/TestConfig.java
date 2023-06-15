package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.repositories.*;
import app.Quiz.jwzpQuizappProject.service.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TestConfig {
    @Bean
    public QuizService quizService() {
        return Mockito.mock(QuizService.class);
    }

    @Bean
    public CategoryService categoryService() {
        return Mockito.mock(CategoryService.class);
    }

    @Bean
    public ResultsService resultsService() {
        return Mockito.mock(ResultsService.class);
    }

    @Bean
    public RoomService roomService() {
        return Mockito.mock(RoomService.class);
    }

    @Bean
    public TokenService tokenService() {
        return Mockito.mock(TokenService.class);
    }

    @Bean
    public QuestionRepository questionRepository() {
        return Mockito.mock(QuestionRepository.class);
    }

    @Bean
    public QuestionAndUsersAnswerRepository questionAndUsersAnswerRepository() {
        return Mockito.mock(QuestionAndUsersAnswerRepository.class);
    }

    @Bean
    public QuizResultsRepository quizResultsRepository() {
        return Mockito.mock(QuizResultsRepository.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public RoomRepository roomRepository() {
        return Mockito.mock(RoomRepository.class);
    }


    @Bean
    public QuizRepository quizRepository() {
        return Mockito.mock(QuizRepository.class);
    }


    @Bean
    public AnswerRepository answerRepository() {
        return Mockito.mock(AnswerRepository.class);
    }


    @Bean
    public CategoryRepository categoryRepository() {
        return Mockito.mock(CategoryRepository.class);
    }


    @Bean
    public ResultsRepository resultsRepository() {
        return Mockito.mock(ResultsRepository.class);
    }

    @Bean
    public Clock clock() {
        return Mockito.mock(Clock.class);
    }

}