package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.repository.UserRepository;
import com.workitech.s19.challenge.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username : " + username + " couldn't found."));
    }


    @Override
    public String getUsername() {
        return UserUtil.getUser(userRepository).getUsername();
    }
}
