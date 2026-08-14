package org.ingsw2526_036.bugboard26backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email:admin@bugboard26.com}")
    private String adminEmail;

    @Value("${app.default-admin.username:admin}")
    private String adminUsername;

    @Value("${app.default-admin.password:adminpassword123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            Administrator admin = new Administrator();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));

            userRepository.save(admin);
            log.info(">>> [DataInitializer] Account Admin di default creato con successo: {} / {}", adminEmail, adminPassword);
        } else {
            log.info(">>> [DataInitializer] Account Admin di default ({}) gia presente nel database.", adminEmail);
        }
    }
}
