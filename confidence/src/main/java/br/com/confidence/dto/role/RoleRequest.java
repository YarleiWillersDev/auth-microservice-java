package br.com.confidence.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(

    @NotBlank(message = "Name cannot be null/empty.")
    @Size(min = 3, message = "Name must contain at least 3 letters")
    String name,

    String description

) {}
