package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.register.RegisterUser;
import com.workitech.s19.challenge.dto.register.ResponseUser;
import com.workitech.s19.challenge.entity.Role;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.UniqueKeyTwitterException;
import com.workitech.s19.challenge.mapper.UserMapper;
import com.workitech.s19.challenge.repository.RoleRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Override
    @Transactional
    public ResponseUser register(RegisterUser in) {
        String username = in.username().trim().toLowerCase();
        String email = in.email().trim().toLowerCase();

        if (userRepository.existsByUsername(username))
            throw new UniqueKeyTwitterException("This username already used by another account " + username, HttpStatus.BAD_REQUEST);

        if (userRepository.existsByEmail(email))
            throw new UniqueKeyTwitterException("This email already used by another account " + email, HttpStatus.BAD_REQUEST);

        Role userRole = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new UniqueKeyTwitterException("Role USER not found", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = new User();
        user.setFirstName(in.firstName());
        user.setLastName(in.lastName());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(in.password()));
        user.setRole(userRole);
        userRepository.save(user);

        return userMapper.toResponse(user, "Registered Successfully");
    }
}
