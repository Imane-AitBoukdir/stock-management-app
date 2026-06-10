package com.example.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService — Unit Tests")
class JwtServiceTest {

    // Same test secret as application-test.properties (base64-encoded)
    private static final String TEST_SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLTEyMzQ1Njc4OTAxMg==";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours

    private JwtService jwtService;
    private UserDetails sampleUserDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, EXPIRATION_MS);
        sampleUserDetails = new User(
                "imane", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // ── generateToken ────────────────────────────────────────────────────────

    @Test
    @DisplayName("generateToken() — produces a non-null, non-empty token")
    void generateToken_producesNonEmptyToken() {
        String token = jwtService.generateToken("imane", "ROLE_USER");

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("generateToken() — embeds the correct username as subject")
    void generateToken_embedsCorrectUsername() {
        String token = jwtService.generateToken("imane", "ROLE_USER");

        assertThat(jwtService.extractUsername(token)).isEqualTo("imane");
    }

    @Test
    @DisplayName("generateToken() — embeds the role claim")
    void generateToken_embedsRoleClaim() {
        String token = jwtService.generateToken("imane", "ROLE_ADMIN");

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertThat(role).isEqualTo("ROLE_ADMIN");
    }

    // ── extractUsername ──────────────────────────────────────────────────────

    @Test
    @DisplayName("extractUsername() — extracts the subject from a valid token")
    void extractUsername_extractsSubject() {
        String token = jwtService.generateToken("testuser", "ROLE_USER");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    // ── isTokenValid ────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid() — returns true for a valid, non-expired token")
    void isTokenValid_returnsTrue_forValidToken() {
        String token = jwtService.generateToken("imane", "ROLE_USER");

        boolean valid = jwtService.isTokenValid(token, sampleUserDetails);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid() — returns false when username does not match")
    void isTokenValid_returnsFalse_whenUsernameMismatch() {
        String token = jwtService.generateToken("otheruser", "ROLE_USER");

        boolean valid = jwtService.isTokenValid(token, sampleUserDetails);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("isTokenValid() — returns false for an expired token")
    void isTokenValid_returnsFalse_forExpiredToken() {
        // Create a JwtService with 0ms expiration to produce an already-expired token
        JwtService shortLivedService = new JwtService(TEST_SECRET, 0L);
        String token = shortLivedService.generateToken("imane", "ROLE_USER");

        // Small delay to ensure expiry
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertThatThrownBy(() -> shortLivedService.isTokenValid(token, sampleUserDetails))
                .isInstanceOf(Exception.class);
    }

    // ── extractClaim ────────────────────────────────────────────────────────

    @Test
    @DisplayName("extractClaim() — extracts expiration date from token")
    void extractClaim_extractsExpiration() {
        String token = jwtService.generateToken("imane", "ROLE_USER");

        Date expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("extractClaim() — extracts issued-at date from token")
    void extractClaim_extractsIssuedAt() {
        Instant before = Instant.now().minusSeconds(1);
        String token = jwtService.generateToken("imane", "ROLE_USER");
        Instant after = Instant.now().plusSeconds(1);

        Date issuedAt = jwtService.extractClaim(token, claims -> claims.getIssuedAt());

        assertThat(issuedAt.toInstant()).isBetween(before, after);
    }

    // ── tampered / malformed tokens ─────────────────────────────────────────

    @Test
    @DisplayName("extractUsername() — throws exception for a tampered token")
    void extractUsername_throwsException_forTamperedToken() {
        String validToken = jwtService.generateToken("imane", "ROLE_USER");
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        assertThatThrownBy(() -> jwtService.extractUsername(tamperedToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("extractUsername() — throws exception for a token signed with different key")
    void extractUsername_throwsException_forDifferentKey() {
        // Generate a token with a different secret key
        String otherSecret = "b3RoZXItc2VjcmV0LWtleS1mb3ItdW5pdC10ZXN0cy0xMjM0NTY3ODkwMTI=";
        SecretKey otherKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(otherSecret));
        String foreignToken = Jwts.builder()
                .subject("imane")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(86400000L)))
                .signWith(otherKey)
                .compact();

        assertThatThrownBy(() -> jwtService.extractUsername(foreignToken))
                .isInstanceOf(Exception.class);
    }
}
