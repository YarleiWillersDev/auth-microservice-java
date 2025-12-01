package br.com.confidence.updater.role;

import org.springframework.stereotype.Component;

import br.com.confidence.dto.role.RoleUpdateRequest;
import br.com.confidence.model.role.Role;

@Component
public class RoleUpdater {

    public void updateRoleInformation(Role role, RoleUpdateRequest updateRequest) {
        updateRoleName(role, updateRequest);
        updateRoleDescription(role, updateRequest);
    }
    
    public void updateRoleName(Role role, RoleUpdateRequest updateRequest) {
        updateRequest.name().ifPresent(role::setName);
    }

    public void updateRoleDescription(Role role, RoleUpdateRequest updateRequest) {
        updateRequest.description().ifPresent(role::setDescription);
    }

}
