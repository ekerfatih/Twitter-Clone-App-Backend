package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.login.LoginRequest;
import com.workitech.s19.challenge.dto.login.LoginResponse;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.TwitterNotFoundException;
import com.workitech.s19.challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        Optional<User> user = userRepository.findUserByUsername(loginRequest.username());
        if (user.isPresent()) {
            String token = jwtService.generateToken(user.get());
            return new LoginResponse("Login successful", auth.getName(), token);
        }
        throw new TwitterNotFoundException("username Not found");
    }

}
