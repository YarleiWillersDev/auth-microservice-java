package br.com.confidence.repository.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.confidence.model.role.Role;
import br.com.confidence.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    List<User> findAllByRolesContaining(Role role);
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
