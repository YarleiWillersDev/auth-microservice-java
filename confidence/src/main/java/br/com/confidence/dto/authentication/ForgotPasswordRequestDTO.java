package br.com.confidence.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequestDTO(

    @NotBlank(message = "Email cannot be null/empty.")
    @Email
    @Size(min = 15, message = "Email must contain at least 15 characters.")
    String email

) {}
