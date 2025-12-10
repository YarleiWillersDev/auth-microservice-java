package br.com.confidence.dto.role;

import java.util.Optional;

public record RoleUpdateRequest (
    Optional<String> name,
    Optional<String> description
) {}
