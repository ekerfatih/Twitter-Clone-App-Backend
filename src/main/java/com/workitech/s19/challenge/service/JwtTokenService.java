package com.workitech.s19.challenge.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenService {
    String generate(UserDetails ud);
    boolean validate(String token);
    String getUsername(String token);
}