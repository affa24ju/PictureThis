package com.PictureThis.PictureThis.JWTsecurity;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JWTUtil {

    // Hemlig nyckel för att signera token, bör vara minst 256 bitar (32 bytes)
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private Key key;

    // Initierar signeringsnyckeln efter att SECRET_KEY har injicerats
    // Base64-dekoderar nyckeln och skapar en Key-instans
    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        key = Keys.hmacShaKeyFor(decodedKey);
    }

    // Genererar en JWT-token med userId som subject
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraherar userId (subject) från token
    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    // Validerar token genom att kolla om den kan parsas och har ett subject
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject() != null;
        } catch (Exception e) {
            return false;
        }
    }

    // Returnerar all data som lagras i payload från en JWT-token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
