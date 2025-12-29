package app.Quiz.jwzpQuizappProject.models.exceptions;

public class ExceptionDto {
    boolean error;
    String message;

    public ExceptionDto(String message) {
        this.message = message;
        this.error = true;
    }

    public ExceptionDto() {
        this.error = true;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
