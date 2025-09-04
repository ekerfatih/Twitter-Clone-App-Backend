package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.login.LoginRequest;
import com.workitech.s19.challenge.dto.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        String token = jwtService.generateToken(auth.getName());
        return new LoginResponse("Login successful", auth.getName(), token);
    }

}
