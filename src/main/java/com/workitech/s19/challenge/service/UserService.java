package com.workitech.s19.challenge.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    String getUsername();
}
