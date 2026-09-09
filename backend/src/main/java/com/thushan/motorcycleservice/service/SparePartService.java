package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.SparePartRequestDTO;
import com.thushan.motorcycleservice.dto.response.SparePartResponseDTO;

import java.util.List;

public interface SparePartService {
    SparePartResponseDTO createSparePart(SparePartRequestDTO request);
    List<SparePartResponseDTO> getAllSpareParts();
    SparePartResponseDTO getSparePartById(Long id);
    SparePartResponseDTO updateSparePart(Long id, SparePartRequestDTO request);
    void deleteSparePart(Long id);
}
