package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;

    public String generateToken(String subject) {
        return jwtUtil.generateToken(subject);
    }

    public String extractUsername(String token) {
        return jwtUtil.parseUsername(token);
    }

    public boolean isValid(String token, UserDetails userDetails) {
        return jwtUtil.isTokenValid(token, userDetails);
    }
}
