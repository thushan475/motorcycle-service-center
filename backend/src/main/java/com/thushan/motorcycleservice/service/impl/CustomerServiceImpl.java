package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.CustomerRequestDTO;
import com.thushan.motorcycleservice.dto.response.CustomerResponseDTO;
import com.thushan.motorcycleservice.entity.Customer;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.CustomerRepository;
import com.thushan.motorcycleservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        log.info("Execute createCustomer()");
        try {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("A customer with email " + request.getEmail() + " already exists");
            }

            Customer customer = modelMapper.map(request, Customer.class);
            Customer saved = customerRepository.save(customer);
            log.info("Customer created with id: {}", saved.getId());
            return modelMapper.map(saved, CustomerResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in createCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        log.info("Execute getAllCustomers()");
        try {
            return customerRepository.findAll().stream()
                    .map(c -> modelMapper.map(c, CustomerResponseDTO.class))
                    .toList();

        } catch (Exception e) {
            log.error("Error in getAllCustomers() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        log.info("Execute getCustomerById()");
        try {
            Customer customer = findCustomerOrThrow(id);
            return modelMapper.map(customer, CustomerResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in getCustomerById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        log.info("Execute updateCustomer()");
        try {
            Customer customer = findCustomerOrThrow(id);

            if (!customer.getEmail().equalsIgnoreCase(request.getEmail())
                    && customerRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("A customer with email " + request.getEmail() + " already exists");
            }

            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            customer.setAddress(request.getAddress());

            Customer updated = customerRepository.save(customer);
            log.info("Customer updated with id: {}", updated.getId());
            return modelMapper.map(updated, CustomerResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in updateCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CustomerResponseDTO patchCustomer(Long id, CustomerRequestDTO request) {
        log.info("Execute patchCustomer()");
        try {
            Customer customer = findCustomerOrThrow(id);

            if (request.getName() != null) customer.setName(request.getName());
            if (request.getPhone() != null) customer.setPhone(request.getPhone());
            if (request.getAddress() != null) customer.setAddress(request.getAddress());
            if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(customer.getEmail())) {
                if (customerRepository.existsByEmail(request.getEmail())) {
                    throw new DuplicateResourceException("A customer with email " + request.getEmail() + " already exists");
                }
                customer.setEmail(request.getEmail());
            }

            Customer updated = customerRepository.save(customer);
            log.info("Customer patched with id: {}", updated.getId());
            return modelMapper.map(updated, CustomerResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in patchCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Execute deleteCustomer()");
        try {
            Customer customer = findCustomerOrThrow(id);
            customerRepository.delete(customer);
            log.info("Customer deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
