package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.ServiceOrderRequestDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderResponseDTO;

import java.util.List;

public interface ServiceOrderService {
    ServiceOrderResponseDTO createServiceOrder(ServiceOrderRequestDTO request);
    List<ServiceOrderResponseDTO> getAllServiceOrders();
    ServiceOrderResponseDTO getServiceOrderById(Long id);
    List<ServiceOrderResponseDTO> getServiceOrdersByCustomer(Long customerId);
    ServiceOrderResponseDTO updateServiceOrderStatus(Long id, String status);
    void deleteServiceOrder(Long id);
    List<ServiceOrderResponseDTO> getCompletedServiceOrders();
    List<ServiceOrderResponseDTO> getCustomerServiceHistory(Long customerId);
}
