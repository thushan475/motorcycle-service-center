package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.AppointmentRequestDTO;
import com.thushan.motorcycleservice.dto.response.AppointmentResponseDTO;
import com.thushan.motorcycleservice.entity.Appointment;
import com.thushan.motorcycleservice.entity.AppointmentStatus;
import com.thushan.motorcycleservice.entity.Customer;
import com.thushan.motorcycleservice.entity.Motorcycle;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.AppointmentRepository;
import com.thushan.motorcycleservice.repository.CustomerRepository;
import com.thushan.motorcycleservice.repository.MotorcycleRepository;
import com.thushan.motorcycleservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final MotorcycleRepository motorcycleRepository;

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        log.info("Execute createAppointment()");
        try {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            Motorcycle motorcycle = motorcycleRepository.findById(request.getMotorcycleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorcycle not found with id: " + request.getMotorcycleId()));

            Appointment appointment = Appointment.builder()
                    .appointmentDate(request.getAppointmentDate())
                    .appointmentTime(request.getAppointmentTime())
                    .remarks(request.getRemarks())
                    .status(AppointmentStatus.PENDING)
                    .customer(customer)
                    .motorcycle(motorcycle)
                    .build();

            Appointment saved = appointmentRepository.save(appointment);
            log.info("Appointment created with id: {} for customer id: {}", saved.getId(), customer.getId());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createAppointment() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppointments() {
        log.info("Execute getAllAppointments()");
        try {
            return appointmentRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllAppointments() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long id) {
        log.info("Execute getAppointmentById()");
        try {
            return toDto(findAppointmentOrThrow(id));

        } catch (Exception e) {
            log.error("Error in getAppointmentById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByCustomer(Long customerId) {
        log.info("Execute getAppointmentsByCustomer()");
        try {
            return appointmentRepository.findByCustomerId(customerId).stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAppointmentsByCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO request) {
        log.info("Execute updateAppointment()");
        try {
            Appointment appointment = findAppointmentOrThrow(id);

            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            Motorcycle motorcycle = motorcycleRepository.findById(request.getMotorcycleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorcycle not found with id: " + request.getMotorcycleId()));

            appointment.setAppointmentDate(request.getAppointmentDate());
            appointment.setAppointmentTime(request.getAppointmentTime());
            appointment.setRemarks(request.getRemarks());
            appointment.setCustomer(customer);
            appointment.setMotorcycle(motorcycle);

            Appointment updated = appointmentRepository.save(appointment);
            log.info("Appointment updated with id: {}", updated.getId());
            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in updateAppointment() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AppointmentResponseDTO updateAppointmentStatus(Long id, String status) {
        log.info("Execute updateAppointmentStatus()");
        try {
            Appointment appointment = findAppointmentOrThrow(id);

            AppointmentStatus newStatus;
            try {
                newStatus = AppointmentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid appointment status: " + status
                        + ". Allowed values: PENDING, CONFIRMED, COMPLETED, CANCELLED");
            }

            appointment.setStatus(newStatus);
            Appointment updated = appointmentRepository.save(appointment);
            log.info("Appointment id: {} status changed to: {}", updated.getId(), newStatus);
            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in updateAppointmentStatus() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAppointment(Long id) {
        log.info("Execute deleteAppointment()");
        try {
            Appointment appointment = findAppointmentOrThrow(id);
            appointmentRepository.delete(appointment);
            log.info("Appointment deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteAppointment() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Appointment findAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private AppointmentResponseDTO toDto(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus().name(),
                appointment.getRemarks(),
                appointment.getCustomer().getId(),
                appointment.getCustomer().getName(),
                appointment.getMotorcycle().getId(),
                appointment.getMotorcycle().getRegistrationNumber()
        );
    }
}
