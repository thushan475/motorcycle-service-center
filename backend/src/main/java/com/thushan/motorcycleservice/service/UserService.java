package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.EmployeeRequestDTO;
import com.thushan.motorcycleservice.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);
    UserResponseDTO createEmployee(EmployeeRequestDTO request);
    void deleteUser(Long id);
}
