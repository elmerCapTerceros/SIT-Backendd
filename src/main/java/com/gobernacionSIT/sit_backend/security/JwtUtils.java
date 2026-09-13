package com.gobernacionSIT.sit_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // En producción esto debe venir de application.yml, no estar hardcodeado
    private final SecretKey key = Keys.hmacShaKeyFor(
            "cambia-esta-clave-por-una-muy-larga-y-secreta-de-al-menos-32-bytes".getBytes()
    );

    private final long expirationMs = 1000 * 60 * 60 * 8; // 8 horas

    public String generateToken(String userLogin) {
        return Jwts.builder()
                .subject(userLogin)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserLoginFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
