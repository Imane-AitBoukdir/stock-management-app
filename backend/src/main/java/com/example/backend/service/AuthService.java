package com.example.backend.service;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.model.User;
import com.example.backend.dto.AuthResponseDTO;
import com.example.backend.dto.RegisterRequestDTO;
import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.model.Role;


import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // register
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Nom d'utilisateur déjà pris : " + request.getUsername()
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email déjà utilisé : " + request.getEmail()
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        log.info(" Nouvel utilisateur enregistré : {}", user.getUsername());

        List<String> roles = List.of("ROLE_" + user.getRole().name());

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    // Step2 : login
    public AuthResponseDTO login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = jwtUtil.generateAccessToken(
            request.getUsername(), roles
        );
        String refreshToken = jwtUtil.generateRefreshToken(
            request.getUsername()
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Utilisateur non trouvé"
                ));

        log.info(" Connexion réussie : {}", user.getUsername());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<String> roles = List.of("ROLE_" + user.getRole().name());

        String newAccess = jwtUtil.generateAccessToken(username, roles);
        String newRefresh = jwtUtil.generateRefreshToken(username);

        return AuthResponseDTO.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}