package br.com.confidence.validation.role;

import org.springframework.stereotype.Component;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleUpdateRequest;
import br.com.confidence.exception.role.InvalidRoleNameException;

@Component
public class RoleValidation {

    public void validateRoleRequestInformation(RoleRequest roleRequest) {
        validateRoleName(roleRequest.name());
    }

    public void validateRoleUpdateRequestInformation(RoleUpdateRequest roleUpdateRequest) {
        if (roleUpdateRequest.name() != null) {
            validateRoleName(roleUpdateRequest.name());
        }
    }

    public void validateRoleName(String name) {
        if (name.isBlank() || name.length() < 3) {
            throw new InvalidRoleNameException("Role name cannot be null/empty or contain fewer than 3 letters.");
        }
    }

}
