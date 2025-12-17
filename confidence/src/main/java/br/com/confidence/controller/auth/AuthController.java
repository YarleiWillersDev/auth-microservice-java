package br.com.confidence.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.confidence.dto.authentication.AuthenticationRequest;
import br.com.confidence.dto.authentication.AuthenticationResponse;
import br.com.confidence.dto.authentication.ForgotPasswordRequestDTO;
import br.com.confidence.dto.authentication.RegisterRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.security.TokenService;
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
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordRecoveryService passwordRecoveryService;

    public AuthController(UserService userService, AuthService authService, TokenService tokenService, AuthenticationManager authenticationManager, PasswordRecoveryService passwordRecoveryService) {
        this.userService = userService;
        this.authService = authService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register (@Valid @RequestBody RegisterRequest request) {
        UserRequest userRequest = new UserRequest(request.name(), request.email(), request.password());
        UserResponse created = userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login (@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        passwordRecoveryService.requestPasswordReset(requestDTO.email());
        return ResponseEntity.noContent().build();
    }
}
