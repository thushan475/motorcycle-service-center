package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.ServiceRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.ServiceResponseDTO;
import com.thushan.motorcycleservice.service.WorkshopServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final WorkshopServiceService workshopServiceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ServiceResponseDTO> createService(@Valid @RequestBody ServiceRequestDTO request) {
        ServiceResponseDTO service = workshopServiceService.createService(request);
        return new CommonResponse<>(201, "Service created successfully", service);
    }

    @GetMapping
    public CommonResponse<List<ServiceResponseDTO>> getAllServices() {
        List<ServiceResponseDTO> services = workshopServiceService.getAllServices();
        return new CommonResponse<>(200, "Services retrieved successfully", services);
    }

    @GetMapping("/{id}")
    public CommonResponse<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        ServiceResponseDTO service = workshopServiceService.getServiceById(id);
        return new CommonResponse<>(200, "Service retrieved successfully", service);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ServiceResponseDTO> updateService(
            @PathVariable Long id, @Valid @RequestBody ServiceRequestDTO request) {
        ServiceResponseDTO service = workshopServiceService.updateService(id, request);
        return new CommonResponse<>(200, "Service updated successfully", service);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteService(@PathVariable Long id) {
        workshopServiceService.deleteService(id);
        return new CommonResponse<>(200, "Service deleted successfully", null);
    }
}
