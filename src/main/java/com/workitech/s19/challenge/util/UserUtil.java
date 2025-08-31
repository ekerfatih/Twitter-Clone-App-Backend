package com.workitech.s19.challenge.util;

import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public final class UserUtil {
    private UserUtil() {}
    public static User getUser(UserRepository userRepository) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getName() == null) throw new AccessDeniedException("Login required");
        return userRepository.findUserByUsername(a.getName())
                .orElseThrow(() -> new AccessDeniedException("Login required"));
    }
}