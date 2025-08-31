package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.register.RegisterUser;
import com.workitech.s19.challenge.dto.register.ResponseUser;

public interface AuthenticationService {
    ResponseUser register(RegisterUser registerUser);
}
