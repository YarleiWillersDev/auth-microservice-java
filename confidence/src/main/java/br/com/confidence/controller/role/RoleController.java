package br.com.confidence.controller.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleResponse;
import br.com.confidence.dto.role.RoleUpdateRequest;
import br.com.confidence.service.role.RoleService;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse newRole = roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable long id, @Valid @RequestBody RoleUpdateRequest request) {
        RoleResponse updatedRole = roleService.update(request, id);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable long id) {
        roleService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> searchById(@PathVariable long id) {
        RoleResponse role = roleService.searchById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoleResponse>> searchByName(@RequestParam String name) {
        List<RoleResponse> roles = roleService.searchByName(name);
        return ResponseEntity.ok(roles);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> listAll() {
        List<RoleResponse> roles = roleService.listAll();
        return ResponseEntity.ok(roles);
    }
}
