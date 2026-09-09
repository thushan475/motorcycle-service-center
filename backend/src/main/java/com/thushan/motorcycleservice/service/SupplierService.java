package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.SupplierRequestDTO;
import com.thushan.motorcycleservice.dto.response.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {
    SupplierResponseDTO createSupplier(SupplierRequestDTO request);
    List<SupplierResponseDTO> getAllSuppliers();
    SupplierResponseDTO getSupplierById(Long id);
    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request);
    void deleteSupplier(Long id);
}
