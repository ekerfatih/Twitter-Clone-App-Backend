package com.workitech.s19.challenge.security;

import com.workitech.s19.challenge.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService uds;
    private final List<String> publicPatterns;
    private final PathPatternParser parser = new PathPatternParser();

    private boolean isPublic(HttpServletRequest req) {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
        var path = PathContainer.parsePath(req.getRequestURI());
        for (String p : publicPatterns) {
            PathPattern pp = parser.parse(p);
            if (pp.matches(path)) return true;
        }
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return isPublic(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String h = req.getHeader("Authorization");
        log.debug("JWT start uri={} authHeaderPresent={}", uri, h != null);
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            try {
                String username = jwtUtil.parseUsername(token);
                log.debug("JWT parsed username={}", username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails ud = uds.loadUserByUsername(username);
                    if (jwtUtil.isTokenValid(token, ud)) {
                        var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("JWT authenticated username={}", username);
                    } else {
                        log.debug("JWT invalid token");
                    }
                }
            } catch (Exception ex) {
                log.debug("JWT parse/validate error: {}", ex.toString());
            }
        }
        chain.doFilter(req, res);
    }
}
