package br.com.confidence.service.role;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleResponse;
import br.com.confidence.dto.role.RoleUpdateRequest;
import br.com.confidence.exception.role.RoleAlreadyExistsException;
import br.com.confidence.exception.role.RoleNotFoundException;
import br.com.confidence.mapper.role.RoleMapper;
import br.com.confidence.model.role.Role;
import br.com.confidence.repository.role.RoleRepository;
import br.com.confidence.updater.role.RoleUpdater;
import br.com.confidence.validation.role.RoleValidation;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleValidation roleValidation;
    private final RoleUpdater roleUpdater;

    public RoleServiceImpl(RoleRepository roleRepository, RoleValidation roleValidation, RoleUpdater roleUpdater) {
        this.roleRepository = roleRepository;
        this.roleValidation = roleValidation;
        this.roleUpdater = roleUpdater;
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest roleRequest) {
        roleValidation.validateRoleRequestInformation(roleRequest);

        if (roleRepository.existsByName(roleRequest.name())) {
            throw new RoleAlreadyExistsException("Role already exists");
        }

        Role role = RoleMapper.toEntity(roleRequest);
        return RoleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse update(RoleUpdateRequest roleUpdateRequest, long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        validateRoleNameUniquenessOnUpdate(id, roleUpdateRequest.name());
        roleValidation.validateRoleUpdateRequestInformation(roleUpdateRequest);
        roleUpdater.updateRoleInformation(role, roleUpdateRequest);

        return RoleMapper.toResponse(roleRepository.save(role));
    }

    private void validateRoleNameUniquenessOnUpdate(long roleId, String newName) {
        List<Role> roles = roleRepository.findByName(newName);

        for (Role existingRole : roles) {
            if (existingRole.getId() != roleId) {
                throw new RoleAlreadyExistsException("Role name already exists");
            }
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        roleRepository.delete(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse searchById(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        return RoleMapper.toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> searchByName(String name) {
        List<Role> roles = roleRepository.findByNameContainingIgnoreCase(name);
        return RoleMapper.toResponse(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> listAll() {
        List<Role> roles = roleRepository.findAll();
        return RoleMapper.toResponse(roles);
    }
}
