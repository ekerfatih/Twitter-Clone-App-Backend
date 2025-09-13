package com.workitech.s19.challenge.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    @Override
    public String generateToken(UserDetails ud) {
        return Jwts.builder()
                .claims(new HashMap<>())
                .subject(ud.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getKey())
                .compact();
    }

    @Override
    public String findUsername(String token) {
        return exportToken(token, Claims::getSubject);
    }

    @Override
    public boolean tokenControl(String jwt, UserDetails userDetails) {
        final String username = findUsername(jwt);
        boolean isExpirationDateEnd = !exportToken(jwt, Claims::getExpiration).before(new Date());
        boolean isUsernamesEqual = username.equals(userDetails.getUsername());
        return isUsernamesEqual && isExpirationDateEnd;
    }

    private <T> T exportToken(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsTFunction.apply(claims);
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
