package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController — Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Pre-create a user for login tests
        User existing = new User();
        existing.setUsername("existinguser");
        existing.setPassword(passwordEncoder.encode("password123"));
        existing.setRole("ROLE_USER");
        userRepository.save(existing);
    }

    // ── POST /api/auth/register ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/register — returns 201 with token for new user")
    void register_returns201_withToken() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "newuser",
                "password", "securepass"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.role", is("ROLE_USER")));
    }

    @Test
    @DisplayName("POST /api/auth/register — returns 400 when username is taken")
    void register_returns400_whenUsernameTaken() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "existinguser",
                "password", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already taken")));
    }

    @Test
    @DisplayName("POST /api/auth/register — returns 400 when username is too short")
    void register_returns400_whenUsernameTooShort() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "ab",
                "password", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register — returns 400 when password is too short")
    void register_returns400_whenPasswordTooShort() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "validuser",
                "password", "12345"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register — returns 400 when fields are blank")
    void register_returns400_whenFieldsAreBlank() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "",
                "password", ""
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/auth/login ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login — returns 200 with token for valid credentials")
    void login_returns200_withToken() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "existinguser",
                "password", "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                .andExpect(jsonPath("$.username", is("existinguser")));
    }

    @Test
    @DisplayName("POST /api/auth/login — returns 401 with wrong password")
    void login_returns401_withWrongPassword() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "existinguser",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid")));
    }

    @Test
    @DisplayName("POST /api/auth/login — returns 401 when user does not exist")
    void login_returns401_whenUserDoesNotExist() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "ghost",
                "password", "somepassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login — returns 400 when username is blank")
    void login_returns400_whenUsernameBlank() throws Exception {
        Map<String, String> payload = Map.of(
                "username", "",
                "password", "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }
}
