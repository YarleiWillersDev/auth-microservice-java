package br.com.confidence.controller.auth;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.confidence.dto.authentication.AuthenticationRequest;
import br.com.confidence.dto.authentication.AuthenticationResponse;
import br.com.confidence.dto.authentication.ForgotPasswordRequestDTO;
import br.com.confidence.dto.authentication.RegisterRequest;
import br.com.confidence.dto.authentication.ResetPasswordRequestDTO;
import br.com.confidence.dto.authentication.UserMeResponseDTO;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.mapper.user.UserMapper;
import br.com.confidence.model.user.User;
import br.com.confidence.service.auth.AuthService;
import br.com.confidence.service.auth.PasswordRecoveryService;
import br.com.confidence.service.user.UserService;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordRecoveryService passwordRecoveryService;

    public AuthController(UserService userService, AuthService authService, PasswordRecoveryService passwordRecoveryService) {
        this.userService = userService;
        this.authService = authService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register (@Valid @RequestBody RegisterRequest request) {
        UserRequest userRequest = UserMapper.toUserRequest(request);
        UserResponse created = userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login (@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO requestDTO) {
        passwordRecoveryService.requestPasswordReset(requestDTO.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        passwordRecoveryService.resetPassword(requestDTO.token(), requestDTO.newPassword());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserMeResponseDTO> userInformation(@AuthenticationPrincipal User user) {
        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        var dto = new UserMeResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            roles
        );

        return ResponseEntity.ok(dto);
    }
}