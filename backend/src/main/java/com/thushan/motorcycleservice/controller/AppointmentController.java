package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.AppointmentRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.AppointmentResponseDTO;
import com.thushan.motorcycleservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO appointment = appointmentService.createAppointment(request);
        return new CommonResponse<>(201, "Appointment created successfully", appointment);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return new CommonResponse<>(200, "Appointments retrieved successfully", appointments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return new CommonResponse<>(200, "Appointment retrieved successfully", appointment);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<AppointmentResponseDTO>> getAppointmentsByCustomer(@PathVariable Long customerId) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByCustomer(customerId);
        return new CommonResponse<>(200, "Appointments retrieved successfully", appointments);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<AppointmentResponseDTO> updateAppointment(
            @PathVariable Long id, @Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO appointment = appointmentService.updateAppointment(id, request);
        return new CommonResponse<>(200, "Appointment updated successfully", appointment);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<AppointmentResponseDTO> updateAppointmentStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        AppointmentResponseDTO appointment = appointmentService.updateAppointmentStatus(id, body.get("status"));
        return new CommonResponse<>(200, "Appointment status updated successfully", appointment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return new CommonResponse<>(200, "Appointment deleted successfully", null);
    }
}
