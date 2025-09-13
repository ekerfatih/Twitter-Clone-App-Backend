package com.workitech.s19.challenge.mapper;

import com.workitech.s19.challenge.dto.register.ResponseUser;
import com.workitech.s19.challenge.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public ResponseUser toResponse( User user, String message) {
        return new ResponseUser( user.getUsername(), user.getEmail(), message);
    }
}
