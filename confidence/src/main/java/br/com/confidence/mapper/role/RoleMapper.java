package br.com.confidence.mapper.role;

import java.util.List;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleResponse;
import br.com.confidence.model.role.Role;

public final class RoleMapper {

    private RoleMapper() {}

    public static Role toEntity(RoleRequest roleRequest) {
        Role role = new Role();
        role.setName(roleRequest.name());
        role.setDescription(roleRequest.description());
        return role;
    }

    public static RoleResponse toResponse(Role role) {
        return new RoleResponse(
            role.getId(),
            role.getName(),
            role.getDescription()
        );
    }

    public static List<RoleResponse> toResponse(List<Role> roles) {
        return roles.stream()
                    .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                    .toList();
    }

}
