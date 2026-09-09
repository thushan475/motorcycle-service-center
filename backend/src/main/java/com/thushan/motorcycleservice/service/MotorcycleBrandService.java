package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.MotorcycleBrandRequestDTO;
import com.thushan.motorcycleservice.dto.response.MotorcycleBrandResponseDTO;

import java.util.List;

public interface MotorcycleBrandService {
    MotorcycleBrandResponseDTO createBrand(MotorcycleBrandRequestDTO request);
    List<MotorcycleBrandResponseDTO> getAllBrands();
    MotorcycleBrandResponseDTO getBrandById(Long id);
    MotorcycleBrandResponseDTO updateBrand(Long id, MotorcycleBrandRequestDTO request);
    void deleteBrand(Long id);
}
