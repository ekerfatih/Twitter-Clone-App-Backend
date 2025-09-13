package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.login.LoginRequest;
import com.workitech.s19.challenge.dto.login.LoginResponse;
import com.workitech.s19.challenge.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpServletResponse res) {
        LoginResponse out = loginService.login(req);
        ResponseCookie cookie = ResponseCookie.from("access_token", out.token())
                .httpOnly(true)
                .secure(Boolean.parseBoolean(System.getProperty("COOKIE_SECURE", "false")))
                .sameSite(System.getProperty("COOKIE_SAMESITE", "Lax"))
                .path("/")
                .maxAge(60 * 60 * 24)
                .build();
        res.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(Boolean.parseBoolean(System.getProperty("COOKIE_SECURE", "false")))
                .sameSite(System.getProperty("COOKIE_SAMESITE", "Lax"))
                .path("/")
                .maxAge(0)
                .build();
        res.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

}
