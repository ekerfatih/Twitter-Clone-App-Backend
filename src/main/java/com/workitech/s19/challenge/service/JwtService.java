package com.workitech.s19.challenge.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails ud);
    String findUsername(String token);
    boolean tokenControl(String jwtToken, UserDetails userDetails);
}