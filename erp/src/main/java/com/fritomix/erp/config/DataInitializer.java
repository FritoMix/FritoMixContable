package com.fritomix.erp.config;

import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.RoleRepository;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@fritomix.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado. Ejecuta las migraciones primero."));

            String rawPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

            User admin = User.builder()
                    .firstName("Ferney")
                    .lastName("Ipiales")
                    .email("admin@fritomix.com")
                    .password(passwordEncoder.encode(rawPassword))
                    .role(adminRole)
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .build();

            userRepository.save(admin);

        }
    }
}
