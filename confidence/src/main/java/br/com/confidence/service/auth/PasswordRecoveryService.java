package br.com.confidence.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.confidence.exception.user.InvalidUserPasswordException;
import br.com.confidence.exception.user.UserNotFoundException;
import br.com.confidence.model.auth.PasswordResetToken;
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

    public PasswordRecoveryService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void requestPasswordReset(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiryDate(expiry);
        tokenRepository.save(token);

        String resetLink = resetPasswordBaseUrl + "?token=" + tokenValue;

        String subject = "Password reset request";
        String body = "Olá, " + user.getName()
                + "\n\nUse o link abaixo para redefinir sua senha (válido por 1 hora):\n"
                + resetLink
                + "\n\nSe você não solicitou, ignore este e-mail.";

        emailService.sendSimpleMail(user.getEmail(), subject, body);
    }

    public void resetPassword(String tokenValue, String newPassword) {
        var token = tokenRepository.findByToken(tokenValue)
            .orElseThrow(() -> new InvalidUserPasswordException("Invalid password reset token"));
        
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired");
        }

        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token);
    }
}
