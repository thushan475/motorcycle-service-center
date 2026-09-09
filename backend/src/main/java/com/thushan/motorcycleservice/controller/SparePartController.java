package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.SparePartRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.SparePartResponseDTO;
import com.thushan.motorcycleservice.service.SparePartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spare-parts")
@RequiredArgsConstructor
public class SparePartController {

    private final SparePartService sparePartService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<SparePartResponseDTO> createSparePart(@Valid @RequestBody SparePartRequestDTO request) {
        SparePartResponseDTO sparePart = sparePartService.createSparePart(request);
        return new CommonResponse<>(201, "Spare part created successfully", sparePart);
    }

    @GetMapping
    public CommonResponse<List<SparePartResponseDTO>> getAllSpareParts() {
        List<SparePartResponseDTO> spareParts = sparePartService.getAllSpareParts();
        return new CommonResponse<>(200, "Spare parts retrieved successfully", spareParts);
    }

    @GetMapping("/{id}")
    public CommonResponse<SparePartResponseDTO> getSparePartById(@PathVariable Long id) {
        SparePartResponseDTO sparePart = sparePartService.getSparePartById(id);
        return new CommonResponse<>(200, "Spare part retrieved successfully", sparePart);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<SparePartResponseDTO> updateSparePart(
            @PathVariable Long id, @Valid @RequestBody SparePartRequestDTO request) {
        SparePartResponseDTO sparePart = sparePartService.updateSparePart(id, request);
        return new CommonResponse<>(200, "Spare part updated successfully", sparePart);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteSparePart(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return new CommonResponse<>(200, "Spare part deleted successfully", null);
    }
}
