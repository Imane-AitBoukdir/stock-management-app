package com.example.backend.config;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("admin", "admin@stockmanager.local", "Admin123!", Role.ADMIN);
        seedUser("manager", "manager@stockmanager.local", "Manager123!", Role.MANAGER);
        seedUser("user", "user@stockmanager.local", "User123!", Role.USER);
    }

    private void seedUser(String username, String email, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return;
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        userRepository.save(user);
    }
}