package br.com.confidence.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.confidence.model.auth.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Query("select t from PasswordResetToken t join fetch t.user where t.token = :token")
    Optional<PasswordResetToken> findByTokenWithUser(@Param("token") String token);

}
