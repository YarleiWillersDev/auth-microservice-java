package br.com.confidence.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

    @NotBlank(message = "Name cannot be null/empty.")
    @Size(min = 3, message = "Name must contain at least 3 letters")
    String nome,

    @NotBlank(message = "Email cannot be null/empty.")
    @Email
    @Size(min = 15, message = "Email must contain at least 15 characters.")
    String email,

    @NotBlank(message = "Password cannot be null/empty.")
    @Size(min = 12, message = "Password must contain at least 12 characters.")
    String password

) {

}
