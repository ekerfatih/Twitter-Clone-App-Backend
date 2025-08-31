package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.register.RegisterUser;
import com.workitech.s19.challenge.dto.register.ResponseUser;
import com.workitech.s19.challenge.entity.Role;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.UniqueKeyTwitterException;
import com.workitech.s19.challenge.mapper.UserMapper;
import com.workitech.s19.challenge.repository.RoleRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public ResponseUser register(RegisterUser registerUser) {
        if (userRepository.existsByUsername(registerUser.username()))
            throw new UniqueKeyTwitterException("This username already used by another account " + registerUser.username(), HttpStatus.BAD_REQUEST);

        if (userRepository.existsByEmail(registerUser.email()))
            throw new UniqueKeyTwitterException("This email already used by another account " + registerUser.email(), HttpStatus.BAD_REQUEST);

        Role userRole = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new UniqueKeyTwitterException("Role USER not found", HttpStatus.INTERNAL_SERVER_ERROR));

        String encodedPassword = passwordEncoder.encode(registerUser.password());

        User user = new User();
        user.setFirstName(registerUser.firstName());
        user.setLastName(registerUser.lastName());
        user.setUsername(registerUser.username());
        user.setEmail(registerUser.email());
        user.setPassword(encodedPassword);
        user.setRole(userRole);
        userRepository.save(user);

        return userMapper.toResponse(user, "Registered Successfully");
    }

}
