package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.ServiceOrderRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.ServiceOrderResponseDTO;
import com.thushan.motorcycleservice.service.ServiceOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<ServiceOrderResponseDTO> createServiceOrder(@Valid @RequestBody ServiceOrderRequestDTO request) {
        ServiceOrderResponseDTO order = serviceOrderService.createServiceOrder(request);
        return new CommonResponse<>(201, "Service order created successfully", order);
    }

    
    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<ServiceOrderResponseDTO>> getCompletedServiceOrders() {
        List<ServiceOrderResponseDTO> orders = serviceOrderService.getCompletedServiceOrders();
        return new CommonResponse<>(200, "Completed service orders retrieved successfully", orders);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<ServiceOrderResponseDTO>> getAllServiceOrders() {
        List<ServiceOrderResponseDTO> orders = serviceOrderService.getAllServiceOrders();
        return new CommonResponse<>(200, "Service orders retrieved successfully", orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<ServiceOrderResponseDTO> getServiceOrderById(@PathVariable Long id) {
        ServiceOrderResponseDTO order = serviceOrderService.getServiceOrderById(id);
        return new CommonResponse<>(200, "Service order retrieved successfully", order);
    }

    
    @GetMapping("/customer/{customerId}/service-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<ServiceOrderResponseDTO>> getCustomerServiceHistory(@PathVariable Long customerId) {
        List<ServiceOrderResponseDTO> orders = serviceOrderService.getCustomerServiceHistory(customerId);
        return new CommonResponse<>(200, "Customer service history retrieved successfully", orders);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<ServiceOrderResponseDTO>> getServiceOrdersByCustomer(@PathVariable Long customerId) {
        List<ServiceOrderResponseDTO> orders = serviceOrderService.getServiceOrdersByCustomer(customerId);
        return new CommonResponse<>(200, "Service orders retrieved successfully", orders);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<ServiceOrderResponseDTO> updateServiceOrderStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        ServiceOrderResponseDTO order = serviceOrderService.updateServiceOrderStatus(id, body.get("status"));
        return new CommonResponse<>(200, "Service order status updated successfully", order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<Void> deleteServiceOrder(@PathVariable Long id) {
        serviceOrderService.deleteServiceOrder(id);
        return new CommonResponse<>(200, "Service order deleted successfully", null);
    }
}