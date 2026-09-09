package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.AppointmentRequestDTO;
import com.thushan.motorcycleservice.dto.response.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO createAppointment(AppointmentRequestDTO request);
    List<AppointmentResponseDTO> getAllAppointments();
    AppointmentResponseDTO getAppointmentById(Long id);
    List<AppointmentResponseDTO> getAppointmentsByCustomer(Long customerId);
    AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO request);
    AppointmentResponseDTO updateAppointmentStatus(Long id, String status);
    void deleteAppointment(Long id);
}
