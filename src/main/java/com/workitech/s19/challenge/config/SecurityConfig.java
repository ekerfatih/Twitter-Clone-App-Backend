package com.workitech.s19.challenge.config;

import com.workitech.s19.challenge.security.JwtAuthFilter;
import com.workitech.s19.challenge.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {
    private static final String[] PUBLIC = {
            "/auth/login",
            "/register",
            "/health",
            "/actuator/health",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/error"
    };

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil, UserDetailsService uds) {
        return new JwtAuthFilter(jwtUtil, uds, List.of(PUBLIC));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())
                .logout(l -> l.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"message\":\"unauthorized\"}");
                }))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(c -> c.disable())
                .addFilterBefore(jwt, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://twitter-clone-frontend-production-5591.up.railway.app"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Content-Type","Authorization","X-Requested-With"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(Duration.ofHours(1));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
