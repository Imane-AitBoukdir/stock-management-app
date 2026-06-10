package com.example.backend.service;

import com.example.backend.dto.AuthRequestDTO;
import com.example.backend.dto.AuthResponseDTO;
import com.example.backend.dto.RegisterRequestDTO;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private AuthRequestDTO loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO("imane", "password123");
        loginRequest = new AuthRequestDTO("imane", "password123");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("imane");
        savedUser.setPassword("$2a$10$encodedPassword");
        savedUser.setRole("ROLE_USER");
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register() — creates a new user and returns a JWT response")
    void register_createsUserAndReturnsToken() {
        when(userRepository.existsByUsername("imane")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken("imane", "ROLE_USER")).thenReturn("jwt-token-abc");

        AuthResponseDTO response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token-abc");
        assertThat(response.getUsername()).isEqualTo("imane");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() — throws IllegalArgumentException when username is taken")
    void register_throwsException_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("imane")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("register() — trims whitespace from username before saving")
    void register_trimsUsernameBeforeSaving() {
        RegisterRequestDTO requestWithSpaces = new RegisterRequestDTO("  imane  ", "password123");
        when(userRepository.existsByUsername("  imane  ")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");

        User trimmedUser = new User();
        trimmedUser.setId(2L);
        trimmedUser.setUsername("imane");
        trimmedUser.setPassword("$2a$10$encoded");
        trimmedUser.setRole("ROLE_USER");

        when(userRepository.save(any(User.class))).thenReturn(trimmedUser);
        when(jwtService.generateToken("imane", "ROLE_USER")).thenReturn("token");

        AuthResponseDTO response = authService.register(requestWithSpaces);

        assertThat(response.getUsername()).isEqualTo("imane");
    }

    // ── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login() — authenticates and returns a JWT response")
    void login_authenticatesAndReturnsToken() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "imane", "$2a$10$encodedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("imane")).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken("imane", "ROLE_USER")).thenReturn("jwt-login-token");

        AuthResponseDTO response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-login-token");
        assertThat(response.getUsername()).isEqualTo("imane");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("login() — throws BadCredentialsException when credentials are wrong")
    void login_throwsBadCredentials_whenInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login() — throws IllegalArgumentException when user not found post-auth")
    void login_throwsIllegalArgument_whenUserNotFoundAfterAuth() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "ghost", "pass", List.of()
        );
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new AuthRequestDTO("ghost", "pass")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}
