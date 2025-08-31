package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.login.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authManager;
    private final org.springframework.security.web.context.SecurityContextRepository securityContextRepository;


    @Override
    public LoginResponse login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // 2) SecurityContext oluştur ve doldur
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // 3) Context'i HttpSession'a PERSIST ET (kritik)
        securityContextRepository.saveContext(context, request, response);

        // 4) Session'ı garanti oluştur (JSESSIONID üretir)
        request.getSession(true);

        // 5) Cevap
        return new LoginResponse("Login successful", auth.getName());
    }
}
