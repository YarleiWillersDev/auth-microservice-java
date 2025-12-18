package br.com.confidence.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import br.com.confidence.dto.authentication.AuthenticationRequest;
import br.com.confidence.dto.authentication.AuthenticationResponse;
import br.com.confidence.model.user.User;
import br.com.confidence.security.TokenService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthenticationResponse login (AuthenticationRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        var authentication = authenticationManager.authenticate(authToken);
        User user = (User) authentication.getPrincipal();

        String token = tokenService.generateToken(user);
        return new AuthenticationResponse(token);
    }

}
