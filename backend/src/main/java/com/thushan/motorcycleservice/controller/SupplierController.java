package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.SupplierRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.SupplierResponseDTO;
import com.thushan.motorcycleservice.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public CommonResponse<SupplierResponseDTO> createSupplier(@Valid @RequestBody SupplierRequestDTO request) {
        SupplierResponseDTO supplier = supplierService.createSupplier(request);
        return new CommonResponse<>(201, "Supplier created successfully", supplier);
    }

    @GetMapping
    public CommonResponse<List<SupplierResponseDTO>> getAllSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers();
        return new CommonResponse<>(200, "Suppliers retrieved successfully", suppliers);
    }

    @GetMapping("/{id}")
    public CommonResponse<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        SupplierResponseDTO supplier = supplierService.getSupplierById(id);
        return new CommonResponse<>(200, "Supplier retrieved successfully", supplier);
    }

    @PutMapping("/{id}")
    public CommonResponse<SupplierResponseDTO> updateSupplier(
            @PathVariable Long id, @Valid @RequestBody SupplierRequestDTO request) {
        SupplierResponseDTO supplier = supplierService.updateSupplier(id, request);
        return new CommonResponse<>(200, "Supplier updated successfully", supplier);
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return new CommonResponse<>(200, "Supplier deleted successfully", null);
    }
}
