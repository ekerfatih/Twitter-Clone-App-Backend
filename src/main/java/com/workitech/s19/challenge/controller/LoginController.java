package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.login.LoginRequest;
import com.workitech.s19.challenge.dto.login.LoginResponse;
import com.workitech.s19.challenge.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        return ResponseEntity.ok(loginService.login(req.username(), req.password(), request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Max-Age=0");
        return ResponseEntity.noContent().build();
    }
}
