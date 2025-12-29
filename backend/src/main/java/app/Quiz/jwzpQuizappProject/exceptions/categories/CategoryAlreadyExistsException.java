package app.Quiz.jwzpQuizappProject.exceptions.categories;

public class CategoryAlreadyExistsException extends Exception{
    public CategoryAlreadyExistsException() {
        super();
    }

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
