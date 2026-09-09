package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.request.EmployeeRequestDTO;
import com.thushan.motorcycleservice.dto.response.UserResponseDTO;
import com.thushan.motorcycleservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<UserResponseDTO> registerEmployee(@Valid @RequestBody EmployeeRequestDTO request) {
        UserResponseDTO user = userService.createEmployee(request);
        return new CommonResponse<>(201, "Employee registered successfully", user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new CommonResponse<>(200, "Users retrieved successfully", users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return new CommonResponse<>(200, "User retrieved successfully", user);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public CommonResponse<UserResponseDTO> getMyProfile(Authentication authentication) {
        UserResponseDTO user = userService.getUserByUsername(authentication.getName());
        return new CommonResponse<>(200, "Profile retrieved successfully", user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new CommonResponse<>(200, "User deleted successfully", null);
    }
}
