package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.MotorcycleBrandRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.MotorcycleBrandResponseDTO;
import com.thushan.motorcycleservice.service.MotorcycleBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motorcycle-brands")
@RequiredArgsConstructor
public class MotorcycleBrandController {

    private final MotorcycleBrandService brandService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<MotorcycleBrandResponseDTO> createBrand(@Valid @RequestBody MotorcycleBrandRequestDTO request) {
        MotorcycleBrandResponseDTO brand = brandService.createBrand(request);
        return new CommonResponse<>(201, "Motorcycle brand created successfully", brand);
    }

    @GetMapping
    public CommonResponse<List<MotorcycleBrandResponseDTO>> getAllBrands() {
        List<MotorcycleBrandResponseDTO> brands = brandService.getAllBrands();
        return new CommonResponse<>(200, "Motorcycle brands retrieved successfully", brands);
    }

    @GetMapping("/{id}")
    public CommonResponse<MotorcycleBrandResponseDTO> getBrandById(@PathVariable Long id) {
        MotorcycleBrandResponseDTO brand = brandService.getBrandById(id);
        return new CommonResponse<>(200, "Motorcycle brand retrieved successfully", brand);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<MotorcycleBrandResponseDTO> updateBrand(
            @PathVariable Long id, @Valid @RequestBody MotorcycleBrandRequestDTO request) {
        MotorcycleBrandResponseDTO brand = brandService.updateBrand(id, request);
        return new CommonResponse<>(200, "Motorcycle brand updated successfully", brand);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return new CommonResponse<>(200, "Motorcycle brand deleted successfully", null);
    }
}
