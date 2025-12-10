package br.com.confidence.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @NotBlank(message = "Name cannot be null/empty.")
    @Size(min = 3, message = "Name must contain at least 3 letters.")
    String name

) {

}
