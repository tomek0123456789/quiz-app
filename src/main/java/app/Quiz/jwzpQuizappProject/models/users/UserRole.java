package app.Quiz.jwzpQuizappProject.models.users;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    public final String roleString;
    private UserRole(String roleString) {
        this.roleString = roleString;
    }
}
