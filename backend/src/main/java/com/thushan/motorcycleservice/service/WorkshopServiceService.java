package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.ServiceRequestDTO;
import com.thushan.motorcycleservice.dto.response.ServiceResponseDTO;

import java.util.List;

public interface WorkshopServiceService {
    ServiceResponseDTO createService(ServiceRequestDTO request);
    List<ServiceResponseDTO> getAllServices();
    ServiceResponseDTO getServiceById(Long id);
    ServiceResponseDTO updateService(Long id, ServiceRequestDTO request);
    void deleteService(Long id);
}
