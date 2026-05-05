package com.example.backend.security;
import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {

    //Step1: secret key , expiration access and refresh token 
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    //step2: converts secret key into secure cryptographic key 

    private SecretKey getSigningKey(){
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //step3: generating access token 
    public String generateAccessToken(String username, List<String> roles){
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .claim("type", "access")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(getSigningKey())
            .compact();
    }

    //step4: refresh token 
    public String generateRefreshToken(String username){
        return Jwts.builder()
            .subject(username)
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(getSigningKey())
            .compact();
    }

    //step5: extraire username
    public String extractUsername(String token){
        return parseClaims(token).getSubject();
    }

    public List<String> extractRoles(String token){
        Object roles = parseClaims(token).get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
    //step6: valider le token
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expiré : " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token non supporté : " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token malformé : " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Signature invalide : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token vide : " + e.getMessage());
        }
        return false;
    }
    //step7: decode et verifie la signature du token pour extraire les claims
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
