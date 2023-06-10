package app.Quiz.jwzpQuizappProject.services;

import app.Quiz.jwzpQuizappProject.repositories.CategoryRepository;
import app.Quiz.jwzpQuizappProject.service.CategoryService;
import junitparams.Parameters;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    private CategoryService categoryService;
    @BeforeAll
    void init(){
        categoryService = new CategoryService(categoryRepositoryMock);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //wszystkie pola, które zostały oznaczone adnotacją @Mock lub @InjectMocks, zostaną zainicjalizowane jako odpowiednie obiekty typu mock przed uruchomieniem każdego testu.
    }


    @Test
    @Parameters({
            "'', ''",
            ""
    })
    void getSingleCategoryCategoryDoesNotExistThrowsCategoryNotFoundException(){

    }
}


