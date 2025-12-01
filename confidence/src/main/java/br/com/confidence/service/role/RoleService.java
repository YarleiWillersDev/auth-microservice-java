package br.com.confidence.service.role;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleResponse;
import br.com.confidence.dto.role.RoleUpdateRequest;

@Service
public interface RoleService {

    public RoleResponse create(RoleRequest roleRequest);
    public RoleResponse update(RoleUpdateRequest roleUpdateRequest, long id);
    public void delete(RoleRequest roleRequest);
    public RoleResponse searchById(long id);
    public List<RoleResponse> searchByName(String name);
    public List<RoleResponse> listAll();


}
