package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.CustomerRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.CustomerResponseDTO;
import com.thushan.motorcycleservice.service.CustomerService;
import com.thushan.motorcycleservice.service.ServiceOrderService;
import com.thushan.motorcycleservice.dto.response.ServiceOrderResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceOrderService serviceOrderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO customer = customerService.createCustomer(request);
        return new CommonResponse<>(201, "Customer created successfully", customer);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return new CommonResponse<>(200, "Customers retrieved successfully", customers);
    }


    @GetMapping("/{customerId}/service-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<ServiceOrderResponseDTO>> getCustomerServiceHistory(@PathVariable Long customerId) {
        List<ServiceOrderResponseDTO> history = serviceOrderService.getCustomerServiceHistory(customerId);
        return new CommonResponse<>(200, "Customer service history retrieved successfully", history);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return new CommonResponse<>(200, "Customer retrieved successfully", customer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id, @Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO customer = customerService.updateCustomer(id, request);
        return new CommonResponse<>(200, "Customer updated successfully", customer);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<CustomerResponseDTO> patchCustomer(
            @PathVariable Long id, @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO customer = customerService.patchCustomer(id, request);
        return new CommonResponse<>(200, "Customer updated successfully", customer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return new CommonResponse<>(200, "Customer deleted successfully", null);
    }
}
