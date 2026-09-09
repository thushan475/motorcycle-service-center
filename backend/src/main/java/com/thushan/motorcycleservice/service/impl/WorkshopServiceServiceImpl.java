package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.ServiceRequestDTO;
import com.thushan.motorcycleservice.dto.response.ServiceResponseDTO;
import com.thushan.motorcycleservice.entity.Service;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.ServiceRepository;
import com.thushan.motorcycleservice.service.WorkshopServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class WorkshopServiceServiceImpl implements WorkshopServiceService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO request) {
        log.info("Execute createService()");
        try {
            Service service = modelMapper.map(request, Service.class);
            if (service.getActive() == null) {
                service.setActive(true);
            }
            Service saved = serviceRepository.save(service);
            log.info("Workshop service created with id: {}", saved.getId());
            return modelMapper.map(saved, ServiceResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in createService() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ServiceResponseDTO> getAllServices() {
        log.info("Execute getAllServices()");
        try {
            return serviceRepository.findAll().stream()
                    .map(s -> modelMapper.map(s, ServiceResponseDTO.class))
                    .toList();

        } catch (Exception e) {
            log.error("Error in getAllServices() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ServiceResponseDTO getServiceById(Long id) {
        log.info("Execute getServiceById()");
        try {
            return modelMapper.map(findServiceOrThrow(id), ServiceResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in getServiceById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ServiceResponseDTO updateService(Long id, ServiceRequestDTO request) {
        log.info("Execute updateService()");
        try {
            Service service = findServiceOrThrow(id);
            service.setName(request.getName());
            service.setDescription(request.getDescription());
            service.setPrice(request.getPrice());
            if (request.getActive() != null) service.setActive(request.getActive());

            Service updated = serviceRepository.save(service);
            log.info("Workshop service updated with id: {}", updated.getId());
            return modelMapper.map(updated, ServiceResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in updateService() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteService(Long id) {
        log.info("Execute deleteService()");
        try {
            Service service = findServiceOrThrow(id);
            serviceRepository.delete(service);
            log.info("Workshop service deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteService() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Service findServiceOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
    }
}
