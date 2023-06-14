package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.service.CategoryService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    public CategoryService categoryService() {
        return Mockito.mock(CategoryService.class);
    }


}