package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.service.UserService;
import com.workitech.s19.challenge.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public String getUsername() {
        return userService.getUsername();
    }
}
