package it.epicode.gestioneeventi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import it.epicode.gestioneeventi.exceptions.UnauthorizedException;

import java.util.Date;

@Service
public class JWTTools {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationms:86400000}")
    private long expirationMs;

    public String generateToken(Long userId) {
        long now = System.currentTimeMillis();
        
        return Jwts.builder()
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .subject(String.valueOf(userId))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public void verifyToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parse(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Token non valido o scaduto");
        }
    }

    public Long extractIdFromToken(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Long.parseLong(subject);
        } catch (Exception ex) {
            throw new UnauthorizedException("Impossibile estrarre ID dal token");
        }
    }
}

