package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.login.LoginRequest;
import com.workitech.s19.challenge.dto.login.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
}