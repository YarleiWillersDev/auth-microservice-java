package br.com.confidence.controller.auth;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import br.com.confidence.exception.user.UserNotFoundException;
import br.com.confidence.mapper.user.UserMapper;
import br.com.confidence.repository.user.UserRepository;
import br.com.confidence.service.auth.AuthService;
import br.com.confidence.service.auth.PasswordRecoveryService;
import br.com.confidence.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Controller responsible for managing security operations.")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordRecoveryService passwordRecoveryService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, AuthService authService,
            PasswordRecoveryService passwordRecoveryService, UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.passwordRecoveryService = passwordRecoveryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new User", description = "Register a new user by adding them to the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided.", content = @Content)
    })

    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserRequest userRequest = UserMapper.toUserRequest(request);
        UserResponse created = userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in to the system", description = "Log in to the system using token validation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided.", content = @Content)
    })

    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Recover user password", description = "Recovering a password lost by the user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password successfully recovered.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data provided.", content = @Content)
    })

    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO requestDTO) {
        passwordRecoveryService.requestPasswordReset(requestDTO.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password", description = "Changing a password that the user has lost.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data provided.", content = @Content)
    })

    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        passwordRecoveryService.resetPassword(requestDTO.token(), requestDTO.newPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "View user information", description = "View user's personal information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data successfully retrieved.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserMeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized user", content = @Content)
    })

    public ResponseEntity<UserMeResponseDTO> userInformation(@Parameter(hidden = true) Authentication auth) {
        var roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var dto = new UserMeResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles);

        return ResponseEntity.ok(dto);
    }
}