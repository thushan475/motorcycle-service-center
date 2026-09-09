package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.MotorcycleRequestDTO;
import com.thushan.motorcycleservice.dto.response.MotorcycleResponseDTO;
import com.thushan.motorcycleservice.entity.Customer;
import com.thushan.motorcycleservice.entity.Motorcycle;
import com.thushan.motorcycleservice.entity.MotorcycleBrand;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.CustomerRepository;
import com.thushan.motorcycleservice.repository.MotorcycleBrandRepository;
import com.thushan.motorcycleservice.repository.MotorcycleRepository;
import com.thushan.motorcycleservice.service.MotorcycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MotorcycleServiceImpl implements MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;
    private final CustomerRepository customerRepository;
    private final MotorcycleBrandRepository brandRepository;

    @Override
    public MotorcycleResponseDTO createMotorcycle(MotorcycleRequestDTO request) {
        log.info("Execute createMotorcycle()");
        try {
            if (motorcycleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
                throw new DuplicateResourceException(
                        "A motorcycle with registration number " + request.getRegistrationNumber() + " already exists");
            }

            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            MotorcycleBrand brand = brandRepository.findById(request.getMotorcycleBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorcycle brand not found with id: " + request.getMotorcycleBrandId()));

            Motorcycle motorcycle = Motorcycle.builder()
                    .registrationNumber(request.getRegistrationNumber())
                    .chassisNumber(request.getChassisNumber())
                    .engineNumber(request.getEngineNumber())
                    .year(request.getYear())
                    .color(request.getColor())
                    .customer(customer)
                    .motorcycleBrand(brand)
                    .build();

            Motorcycle saved = motorcycleRepository.save(motorcycle);
            log.info("Motorcycle created with id: {} for customer id: {}", saved.getId(), customer.getId());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createMotorcycle() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<MotorcycleResponseDTO> getAllMotorcycles() {
        log.info("Execute getAllMotorcycles()");
        try {
            return motorcycleRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllMotorcycles() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MotorcycleResponseDTO getMotorcycleById(Long id) {
        log.info("Execute getMotorcycleById()");
        try {
            return toDto(findMotorcycleOrThrow(id));

        } catch (Exception e) {
            log.error("Error in getMotorcycleById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<MotorcycleResponseDTO> getMotorcyclesByCustomer(Long customerId) {
        log.info("Execute getMotorcyclesByCustomer()");
        try {
            return motorcycleRepository.findByCustomerId(customerId).stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getMotorcyclesByCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MotorcycleResponseDTO updateMotorcycle(Long id, MotorcycleRequestDTO request) {
        log.info("Execute updateMotorcycle()");
        try {
            Motorcycle motorcycle = findMotorcycleOrThrow(id);

            if (!motorcycle.getRegistrationNumber().equalsIgnoreCase(request.getRegistrationNumber())
                    && motorcycleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
                throw new DuplicateResourceException(
                        "A motorcycle with registration number " + request.getRegistrationNumber() + " already exists");
            }

            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            MotorcycleBrand brand = brandRepository.findById(request.getMotorcycleBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorcycle brand not found with id: " + request.getMotorcycleBrandId()));

            motorcycle.setRegistrationNumber(request.getRegistrationNumber());
            motorcycle.setChassisNumber(request.getChassisNumber());
            motorcycle.setEngineNumber(request.getEngineNumber());
            motorcycle.setYear(request.getYear());
            motorcycle.setColor(request.getColor());
            motorcycle.setCustomer(customer);
            motorcycle.setMotorcycleBrand(brand);

            Motorcycle updated = motorcycleRepository.save(motorcycle);
            log.info("Motorcycle updated with id: {}", updated.getId());
            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in updateMotorcycle() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MotorcycleResponseDTO patchMotorcycle(Long id, MotorcycleRequestDTO request) {
        log.info("Execute patchMotorcycle()");
        try {
            Motorcycle motorcycle = findMotorcycleOrThrow(id);

            if (request.getColor() != null) motorcycle.setColor(request.getColor());
            if (request.getYear() != null) motorcycle.setYear(request.getYear());
            if (request.getChassisNumber() != null) motorcycle.setChassisNumber(request.getChassisNumber());
            if (request.getEngineNumber() != null) motorcycle.setEngineNumber(request.getEngineNumber());

            Motorcycle updated = motorcycleRepository.save(motorcycle);
            log.info("Motorcycle patched with id: {}", updated.getId());
            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in patchMotorcycle() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteMotorcycle(Long id) {
        log.info("Execute deleteMotorcycle()");
        try {
            Motorcycle motorcycle = findMotorcycleOrThrow(id);
            motorcycleRepository.delete(motorcycle);
            log.info("Motorcycle deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteMotorcycle() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Motorcycle findMotorcycleOrThrow(Long id) {
        return motorcycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorcycle not found with id: " + id));
    }

    private MotorcycleResponseDTO toDto(Motorcycle motorcycle) {
        return new MotorcycleResponseDTO(
                motorcycle.getId(),
                motorcycle.getRegistrationNumber(),
                motorcycle.getChassisNumber(),
                motorcycle.getEngineNumber(),
                motorcycle.getYear(),
                motorcycle.getColor(),
                motorcycle.getCustomer().getId(),
                motorcycle.getCustomer().getName(),
                motorcycle.getMotorcycleBrand().getId(),
                motorcycle.getMotorcycleBrand().getName()
        );
    }
}
