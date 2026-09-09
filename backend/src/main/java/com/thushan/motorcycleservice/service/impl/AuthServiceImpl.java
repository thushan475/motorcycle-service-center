package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.LoginRequestDTO;
import com.thushan.motorcycleservice.dto.request.RegisterRequestDTO;
import com.thushan.motorcycleservice.dto.response.LoginResponseDTO;
import com.thushan.motorcycleservice.dto.response.UserResponseDTO;
import com.thushan.motorcycleservice.entity.Role;
import com.thushan.motorcycleservice.entity.User;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.RoleRepository;
import com.thushan.motorcycleservice.repository.UserRepository;
import com.thushan.motorcycleservice.service.AuthService;
import com.thushan.motorcycleservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponseDTO register(RegisterRequestDTO request) {
        log.info("Execute register()");
        try {
            log.info("Registering new user with username: {}", request.getUsername());

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already taken: " + request.getUsername());
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already registered: " + request.getEmail());
            }

            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "USER role not found. Make sure roles are seeded on startup."));

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(userRole)
                    .enabled(true)
                    .build();

            User saved = userRepository.save(user);
            log.info("User registered successfully: {}", saved.getUsername());

            UserResponseDTO response = modelMapper.map(saved, UserResponseDTO.class);
            response.setRole(saved.getRole().getName());
            return response;

        } catch (Exception e) {
            log.error("Error in register() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Execute login()");
        try {
            log.info("Login attempt for username: {}", request.getUsername());

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );
            } catch (BadCredentialsException e) {
                log.warn("Failed login attempt for username: {}", request.getUsername());
                throw new BadCredentialsException("Invalid username or password");
            }

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUsername()));

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());
            log.info("Login successful for username: {}", user.getUsername());

            return new LoginResponseDTO(token, user.getUsername(), user.getRole().getName());

        } catch (Exception e) {
            log.error("Error in login() : " + e.getMessage(), e);
            throw e;
        }
    }

}
