package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.MotorcycleRequestDTO;
import com.thushan.motorcycleservice.dto.response.MotorcycleResponseDTO;

import java.util.List;

public interface MotorcycleService {
    MotorcycleResponseDTO createMotorcycle(MotorcycleRequestDTO request);
    List<MotorcycleResponseDTO> getAllMotorcycles();
    MotorcycleResponseDTO getMotorcycleById(Long id);
    List<MotorcycleResponseDTO> getMotorcyclesByCustomer(Long customerId);
    MotorcycleResponseDTO updateMotorcycle(Long id, MotorcycleRequestDTO request);
    MotorcycleResponseDTO patchMotorcycle(Long id, MotorcycleRequestDTO request);
    void deleteMotorcycle(Long id);
}
