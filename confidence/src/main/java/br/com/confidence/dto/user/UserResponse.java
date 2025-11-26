package br.com.confidence.dto.user;

import java.time.LocalDateTime;
import java.util.List;

import br.com.confidence.dto.role.RoleResponse;

public record UserResponse(

    long id,

    String name,

    String email,

    LocalDateTime createdAt,

    LocalDateTime updatedATime,

    List<RoleResponse> roles

) {

}
