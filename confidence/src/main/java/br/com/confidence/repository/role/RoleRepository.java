package br.com.confidence.repository.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.confidence.model.role.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameContainingIgnoreCase(String name);
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    long countByName(String name);

}
