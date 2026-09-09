package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.SupplierRequestDTO;
import com.thushan.motorcycleservice.dto.response.SupplierResponseDTO;
import com.thushan.motorcycleservice.entity.Supplier;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.SupplierRepository;
import com.thushan.motorcycleservice.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    @Override
    public SupplierResponseDTO createSupplier(SupplierRequestDTO request) {
        log.info("Execute createSupplier()");
        try {
            Supplier supplier = modelMapper.map(request, Supplier.class);
            Supplier saved = supplierRepository.save(supplier);
            log.info("Supplier created with id: {}", saved.getId());
            return modelMapper.map(saved, SupplierResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in createSupplier() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<SupplierResponseDTO> getAllSuppliers() {
        log.info("Execute getAllSuppliers()");
        try {
            return supplierRepository.findAll().stream()
                    .map(s -> modelMapper.map(s, SupplierResponseDTO.class))
                    .toList();

        } catch (Exception e) {
            log.error("Error in getAllSuppliers() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SupplierResponseDTO getSupplierById(Long id) {
        log.info("Execute getSupplierById()");
        try {
            return modelMapper.map(findSupplierOrThrow(id), SupplierResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in getSupplierById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request) {
        log.info("Execute updateSupplier()");
        try {
            Supplier supplier = findSupplierOrThrow(id);
            supplier.setName(request.getName());
            supplier.setEmail(request.getEmail());
            supplier.setPhone(request.getPhone());
            supplier.setAddress(request.getAddress());

            Supplier updated = supplierRepository.save(supplier);
            log.info("Supplier updated with id: {}", updated.getId());
            return modelMapper.map(updated, SupplierResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in updateSupplier() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteSupplier(Long id) {
        log.info("Execute deleteSupplier()");
        try {
            Supplier supplier = findSupplierOrThrow(id);
            supplierRepository.delete(supplier);
            log.info("Supplier deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteSupplier() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Supplier findSupplierOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }
}
