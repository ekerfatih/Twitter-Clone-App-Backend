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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax") // cross-site ise "None"
                .maxAge(0)
                .build();
        res.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
