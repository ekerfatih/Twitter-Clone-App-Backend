package com.workitech.s19.challenge.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret.hex:e0f23f89bb30fffe1e05803b2e6dd32065c4b48b80d79af633f3bb88f84669ca}")
    private String secretHex;

    @Value("${security.jwt.issuer:Twitter}")
    private String issuer;

    @Value("${security.jwt.ttlMillis:6000000}")
    private long ttlMillis;

    private SecretKey key;

    @PostConstruct
    void init() {
        key = Keys.hmacShaKeyFor(hexToBytes(secretHex));
    }

    public String generateToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(new Date(now))
                .expiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
    }

    public String parseUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims c = claims(token);
        return userDetails.getUsername().equals(c.getSubject()) && c.getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((toNibble(hex.charAt(i)) << 4) | toNibble(hex.charAt(i + 1)));
        }
        return out;
    }

    private int toNibble(char c) {
        int d = Character.digit(c, 16);
        if (d < 0) throw new IllegalArgumentException("Invalid hex");
        return d;
    }
}
