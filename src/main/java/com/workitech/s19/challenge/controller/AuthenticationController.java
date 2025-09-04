package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.register.RegisterUser;
import com.workitech.s19.challenge.dto.register.ResponseUser;
import com.workitech.s19.challenge.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseUser register(@RequestBody RegisterUser registerUser) {
        return authenticationService.register(registerUser);
    }


}
