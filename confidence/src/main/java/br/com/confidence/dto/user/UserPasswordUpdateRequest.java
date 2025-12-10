package br.com.confidence.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateRequest(

    @NotBlank(message = "Password cannot be null/empty.")
    @Size(min = 12, message = "Password must contain at least 12 characters.")
    String currentPassword,

    @NotBlank(message = "Password cannot be null/empty.")
    @Size(min = 12, message = "Password must contain at least 12 characters.")
    String newPassword

) {

}

