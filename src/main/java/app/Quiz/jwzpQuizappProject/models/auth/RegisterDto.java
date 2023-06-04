package app.Quiz.jwzpQuizappProject.models.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {}
