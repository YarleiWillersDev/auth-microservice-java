package br.com.confidence.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.confidence.exception.auth.PasswordResetTokenExpiredException;
import br.com.confidence.exception.user.InvalidUserPasswordException;
import br.com.confidence.model.auth.PasswordResetToken;
import br.com.confidence.model.user.User;
import br.com.confidence.repository.auth.PasswordResetTokenRepository;
import br.com.confidence.repository.user.UserRepository;
import br.com.confidence.service.email.EmailService;

@Service
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.reset-password-url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    public PasswordRecoveryService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository,
            EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(this::sendResetEmailForUser);
    }

    private void sendResetEmailForUser(User user) {
        String tokenValue = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(1);

            PasswordResetToken token = new PasswordResetToken();
            token.setToken(tokenValue);
            token.setUser(user);
            token.setExpiryDate(expiry);
            tokenRepository.save(token);

            String resetLink = resetPasswordBaseUrl + "?token=" + tokenValue;

            String subject = "Password reset request";
            String body = "Hello, " + user.getName()
                    + "\n\nUse the link below to reset your password (valid for 1 hour):\n"
                    + resetLink
                    + "\n\nIf you did not request this, please ignore this email.";

            emailService.sendSimpleMail(user.getEmail(), subject, body);
    }

    public void resetPassword(String tokenValue, String newPassword) {
        var token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidUserPasswordException("Invalid or expired password reset token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new PasswordResetTokenExpiredException("Password reset token has expired");
        }

        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token);
    }
}
