package app.Quiz.jwzpQuizappProject.models.responseEntities;


public class LoginResponseEntity {

    String token;

    public LoginResponseEntity(String token) {
        this.token = token;
    }

    public LoginResponseEntity() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
