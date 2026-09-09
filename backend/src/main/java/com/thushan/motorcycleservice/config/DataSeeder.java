package com.thushan.motorcycleservice.config;

import com.thushan.motorcycleservice.entity.MotorcycleBrand;
import com.thushan.motorcycleservice.entity.Role;
import com.thushan.motorcycleservice.entity.User;
import com.thushan.motorcycleservice.repository.MotorcycleBrandRepository;
import com.thushan.motorcycleservice.repository.RoleRepository;
import com.thushan.motorcycleservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MotorcycleBrandRepository motorcycleBrandRepository;

    private static final List<String> COMMON_BRANDS = List.of(
            "Honda",
            "Yamaha",
            "Suzuki",
            "Kawasaki",
            "Bajaj",
            "TVS",
            "Hero",
            "Royal Enfield",
            "KTM",
            "Ducati",
            "Vespa",
            "Demak"
    );

    @Override
    public void run(String... args) {
        Role adminRole = seedRole("ADMIN");
        seedRole("USER");
        seedRole("GUEST");

        seedMotorcycleBrands();

        if (userRepository.existsByUsername("admin")) {
            log.info("Default admin account already exists, skipping.");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@motorcycleservice.local")
                .password(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .enabled(true)
                .build();

        userRepository.save(admin);
        log.info("Default admin account created. username=admin, password=admin123 " +
                "(please change this after first login in a real deployment)");
    }

    private Role seedRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role saved = roleRepository.save(Role.builder().name(name).build());
                    log.info("Seeded role: {}", name);
                    return saved;
                });
    }

    private void seedMotorcycleBrands() {
        for (String name : COMMON_BRANDS) {
            if (!motorcycleBrandRepository.existsByName(name)) {
                motorcycleBrandRepository.save(
                        MotorcycleBrand.builder()
                                .name(name)
                                .build()
                );
                log.info("Seeded motorcycle brand: {}", name);
            }
        }
    }
}