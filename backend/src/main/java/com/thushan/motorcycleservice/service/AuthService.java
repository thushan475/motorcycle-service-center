package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.LoginRequestDTO;
import com.thushan.motorcycleservice.dto.request.RegisterRequestDTO;
import com.thushan.motorcycleservice.dto.response.LoginResponseDTO;
import com.thushan.motorcycleservice.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(RegisterRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
}
