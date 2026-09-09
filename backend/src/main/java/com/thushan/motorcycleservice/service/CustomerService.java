package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.CustomerRequestDTO;
import com.thushan.motorcycleservice.dto.response.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO request);
    List<CustomerResponseDTO> getAllCustomers();
    CustomerResponseDTO getCustomerById(Long id);
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request);
    CustomerResponseDTO patchCustomer(Long id, CustomerRequestDTO request);
    void deleteCustomer(Long id);
}
