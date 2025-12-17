package br.com.confidence.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(

    @NotBlank(message = "Password cannot be null/empty.")
    String token,

    @NotBlank(message = "Password cannot be null/empty.")
    @Size(min = 12, message = "Password must contain at least 12 characters.")
    String newPassword
) {}
