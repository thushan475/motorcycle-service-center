package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.MotorcycleRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.MotorcycleResponseDTO;
import com.thushan.motorcycleservice.service.MotorcycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motorcycles")
@RequiredArgsConstructor
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<MotorcycleResponseDTO> createMotorcycle(@Valid @RequestBody MotorcycleRequestDTO request) {
        MotorcycleResponseDTO motorcycle = motorcycleService.createMotorcycle(request);
        return new CommonResponse<>(201, "Motorcycle created successfully", motorcycle);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<MotorcycleResponseDTO>> getAllMotorcycles() {
        List<MotorcycleResponseDTO> motorcycles = motorcycleService.getAllMotorcycles();
        return new CommonResponse<>(200, "Motorcycles retrieved successfully", motorcycles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<MotorcycleResponseDTO> getMotorcycleById(@PathVariable Long id) {
        MotorcycleResponseDTO motorcycle = motorcycleService.getMotorcycleById(id);
        return new CommonResponse<>(200, "Motorcycle retrieved successfully", motorcycle);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<MotorcycleResponseDTO>> getMotorcyclesByCustomer(@PathVariable Long customerId) {
        List<MotorcycleResponseDTO> motorcycles = motorcycleService.getMotorcyclesByCustomer(customerId);
        return new CommonResponse<>(200, "Motorcycles retrieved successfully", motorcycles);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<MotorcycleResponseDTO> updateMotorcycle(
            @PathVariable Long id, @Valid @RequestBody MotorcycleRequestDTO request) {
        MotorcycleResponseDTO motorcycle = motorcycleService.updateMotorcycle(id, request);
        return new CommonResponse<>(200, "Motorcycle updated successfully", motorcycle);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<MotorcycleResponseDTO> patchMotorcycle(
            @PathVariable Long id, @RequestBody MotorcycleRequestDTO request) {
        MotorcycleResponseDTO motorcycle = motorcycleService.patchMotorcycle(id, request);
        return new CommonResponse<>(200, "Motorcycle updated successfully", motorcycle);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteMotorcycle(@PathVariable Long id) {
        motorcycleService.deleteMotorcycle(id);
        return new CommonResponse<>(200, "Motorcycle deleted successfully", null);
    }
}
