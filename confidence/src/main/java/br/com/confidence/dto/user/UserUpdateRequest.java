package br.com.confidence.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @NotBlank(message = "Name cannot be null/empty.")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters long\"")
    String name

) {

}
