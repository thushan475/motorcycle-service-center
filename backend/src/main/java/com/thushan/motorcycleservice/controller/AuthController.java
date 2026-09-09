package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.LoginRequestDTO;
import com.thushan.motorcycleservice.dto.request.RegisterRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.LoginResponseDTO;
import com.thushan.motorcycleservice.dto.response.UserResponseDTO;
import com.thushan.motorcycleservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public CommonResponse<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        UserResponseDTO user = authService.register(request);
        return new CommonResponse<>(201, "User registered successfully", user);
    }

    @PostMapping("/login")
    public CommonResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return new CommonResponse<>(200, "Login successful", response);
    }
}
