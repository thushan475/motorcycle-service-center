package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.MotorcycleBrandRequestDTO;
import com.thushan.motorcycleservice.dto.response.MotorcycleBrandResponseDTO;
import com.thushan.motorcycleservice.entity.MotorcycleBrand;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.MotorcycleBrandRepository;
import com.thushan.motorcycleservice.service.MotorcycleBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MotorcycleBrandServiceImpl implements MotorcycleBrandService {

    private final MotorcycleBrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public MotorcycleBrandResponseDTO createBrand(MotorcycleBrandRequestDTO request) {
        log.info("Execute createBrand()");
        try {
            if (brandRepository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Motorcycle brand already exists: " + request.getName());
            }
            MotorcycleBrand brand = modelMapper.map(request, MotorcycleBrand.class);
            MotorcycleBrand saved = brandRepository.save(brand);
            log.info("Motorcycle brand created with id: {}", saved.getId());
            return modelMapper.map(saved, MotorcycleBrandResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in createBrand() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<MotorcycleBrandResponseDTO> getAllBrands() {
        log.info("Execute getAllBrands()");
        try {
            return brandRepository.findAll().stream()
                    .map(b -> modelMapper.map(b, MotorcycleBrandResponseDTO.class))
                    .toList();

        } catch (Exception e) {
            log.error("Error in getAllBrands() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MotorcycleBrandResponseDTO getBrandById(Long id) {
        log.info("Execute getBrandById()");
        try {
            MotorcycleBrand brand = findBrandOrThrow(id);
            return modelMapper.map(brand, MotorcycleBrandResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in getBrandById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MotorcycleBrandResponseDTO updateBrand(Long id, MotorcycleBrandRequestDTO request) {
        log.info("Execute updateBrand()");
        try {
            MotorcycleBrand brand = findBrandOrThrow(id);
            brand.setName(request.getName());
            brand.setDescription(request.getDescription());
            MotorcycleBrand updated = brandRepository.save(brand);
            log.info("Motorcycle brand updated with id: {}", updated.getId());
            return modelMapper.map(updated, MotorcycleBrandResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in updateBrand() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteBrand(Long id) {
        log.info("Execute deleteBrand()");
        try {
            MotorcycleBrand brand = findBrandOrThrow(id);
            brandRepository.delete(brand);
            log.info("Motorcycle brand deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteBrand() : " + e.getMessage(), e);
            throw e;
        }
    }

    private MotorcycleBrand findBrandOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorcycle brand not found with id: " + id));
    }
}
