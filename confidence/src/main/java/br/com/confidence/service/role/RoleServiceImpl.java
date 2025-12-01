package br.com.confidence.service.role;

import java.util.List;

import org.springframework.stereotype.Service;

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
import jakarta.transaction.Transactional;

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

    @Transactional
    @Override
    public RoleResponse create(RoleRequest roleRequest) {
        roleValidation.validateRoleRequestInformation(roleRequest);

        if (roleRepository.existsByName(roleRequest.name())) {
            throw new RoleAlreadyExistsException("Role already exists");
        }

        Role role = RoleMapper.toEntity(roleRequest);
        return RoleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse update(RoleUpdateRequest roleUpdateRequest, long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        roleValidation.validateRoleUpdateRequestInformation(roleUpdateRequest);
        roleUpdater.updateRoleInformation(role, roleUpdateRequest);

        return RoleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public void delete(RoleRequest roleRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public RoleResponse searchById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchById'");
    }

    @Override
    public List<RoleResponse> searchByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByName'");
    }

    @Override
    public List<RoleResponse> listAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAll'");
    }

}
