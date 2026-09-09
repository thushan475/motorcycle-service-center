package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.EmployeeRequestDTO;
import com.thushan.motorcycleservice.dto.response.UserResponseDTO;
import com.thushan.motorcycleservice.entity.Role;
import com.thushan.motorcycleservice.entity.User;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.RoleRepository;
import com.thushan.motorcycleservice.repository.UserRepository;
import com.thushan.motorcycleservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.info("Execute getAllUsers()");
        try {
            return userRepository.findAll().stream()
                    .map(this::toDto)
                    .toList();

        } catch (Exception e) {
            log.error("Error in getAllUsers() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        log.info("Execute getUserById()");
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            return toDto(user);

        } catch (Exception e) {
            log.error("Error in getUserById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        log.info("Execute getUserByUsername()");
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
            return toDto(user);

        } catch (Exception e) {
            log.error("Error in getUserByUsername() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserResponseDTO createEmployee(EmployeeRequestDTO request) {
        log.info("Execute createEmployee()");
        try {
            log.info("Admin registering new employee with username: {}", request.getUsername());

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already taken: " + request.getUsername());
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already registered: " + request.getEmail());
            }

            Role role = roleRepository.findByName(request.getRole().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role not found: " + request.getRole() + ". Make sure roles are seeded on startup."));

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .enabled(true)
                    .build();

            User saved = userRepository.save(user);
            log.info("Employee registered successfully: {} with role: {}", saved.getUsername(), role.getName());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createEmployee() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Execute deleteUser()");
        try {
            if (!userRepository.existsById(id)) {
                throw new ResourceNotFoundException("User not found with id: " + id);
            }
            userRepository.deleteById(id);
            log.info("User deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteUser() : " + e.getMessage(), e);
            throw e;
        }
    }

    private UserResponseDTO toDto(User user) {
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        dto.setRole(user.getRole().getName());
        return dto;
    }
}
