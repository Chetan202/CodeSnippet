package com.example.CodeHub.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMillis;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration:86400000}") long expirationMillis) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && claims(token).getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
