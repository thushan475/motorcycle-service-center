package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.InvoiceRequestDTO;
import com.thushan.motorcycleservice.dto.request.ServiceOrderItemRequestDTO;
import com.thushan.motorcycleservice.dto.request.ServiceOrderRequestDTO;
import com.thushan.motorcycleservice.dto.request.ServiceOrderSparePartRequestDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderItemResponseDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderResponseDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderSparePartResponseDTO;
import com.thushan.motorcycleservice.entity.*;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.*;
import com.thushan.motorcycleservice.service.InvoiceService;
import com.thushan.motorcycleservice.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final ServiceRepository serviceRepository;
    private final SparePartRepository sparePartRepository;
    private final InventoryRepository inventoryRepository;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public ServiceOrderResponseDTO createServiceOrder(ServiceOrderRequestDTO request) {
        log.info("Execute createServiceOrder()");
        try {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            Motorcycle motorcycle = motorcycleRepository.findById(request.getMotorcycleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorcycle not found with id: " + request.getMotorcycleId()));

            ServiceOrder order = ServiceOrder.builder()
                    .orderDate(LocalDate.now())
                    .status(ServiceOrderStatus.OPEN)
                    .remarks(request.getRemarks())
                    .customer(customer)
                    .motorcycle(motorcycle)
                    .items(new ArrayList<>())
                    .build();

            BigDecimal total = BigDecimal.ZERO;
            for (ServiceOrderItemRequestDTO itemRequest : request.getItems()) {
                Service service = serviceRepository.findById(itemRequest.getServiceId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + itemRequest.getServiceId()));

                BigDecimal subtotal = service.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                ServiceOrderItem item = ServiceOrderItem.builder()
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(service.getPrice())
                        .subtotal(subtotal)
                        .service(service)
                        .serviceOrder(order)
                        .build();

                order.getItems().add(item);
                total = total.add(subtotal);
            }

            if (request.getSpareParts() != null) {
                for (ServiceOrderSparePartRequestDTO sparePartRequest : request.getSpareParts()) {
                    SparePart sparePart = sparePartRepository.findById(sparePartRequest.getSparePartId())
                            .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with id: " + sparePartRequest.getSparePartId()));

                    Inventory inventory = inventoryRepository.findBySparePartId(sparePart.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("No inventory record found for spare part id: " + sparePart.getId()));

                    if (inventory.getQuantity() < sparePartRequest.getQuantity()) {
                        throw new BadRequestException("Insufficient stock for spare part: " + sparePart.getName()
                                + ". Available: " + inventory.getQuantity() + ", requested: " + sparePartRequest.getQuantity());
                    }

                    BigDecimal sparePartSubtotal = sparePart.getSellingPrice().multiply(BigDecimal.valueOf(sparePartRequest.getQuantity()));

                    ServiceOrderSparePart orderSparePart = ServiceOrderSparePart.builder()
                            .quantity(sparePartRequest.getQuantity())
                            .unitPrice(sparePart.getSellingPrice())
                            .subtotal(sparePartSubtotal)
                            .sparePart(sparePart)
                            .serviceOrder(order)
                            .build();

                    order.getSpareParts().add(orderSparePart);
                    total = total.add(sparePartSubtotal);

                    inventory.setQuantity(inventory.getQuantity() - sparePartRequest.getQuantity());
                    inventoryRepository.saveAndFlush(inventory);
                }
            }

            order.setTotalAmount(total);

            ServiceOrder saved = serviceOrderRepository.save(order);
            log.info("Service order created with id: {}, total amount: {}", saved.getId(), saved.getTotalAmount());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createServiceOrder() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ServiceOrderResponseDTO> getAllServiceOrders() {
        log.info("Execute getAllServiceOrders()");
        try {
            return serviceOrderRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllServiceOrders() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ServiceOrderResponseDTO getServiceOrderById(Long id) {
        log.info("Execute getServiceOrderById()");
        try {
            return toDto(findOrderOrThrow(id));

        } catch (Exception e) {
            log.error("Error in getServiceOrderById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ServiceOrderResponseDTO> getServiceOrdersByCustomer(Long customerId) {
        log.info("Execute getServiceOrdersByCustomer()");
        try {
            return serviceOrderRepository.findByCustomerId(customerId).stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getServiceOrdersByCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ServiceOrderResponseDTO updateServiceOrderStatus(Long id, String status) {
        log.info("Execute updateServiceOrderStatus()");
        try {
            ServiceOrder order = findOrderOrThrow(id);

            ServiceOrderStatus newStatus;
            try {
                newStatus = ServiceOrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid service order status: " + status
                        + ". Allowed values: OPEN, IN_PROGRESS, COMPLETED, CANCELLED");
            }

            order.setStatus(newStatus);
            ServiceOrder updated = serviceOrderRepository.save(order);
            log.info("Service order id: {} status changed to: {}", updated.getId(), newStatus);

            if (newStatus == ServiceOrderStatus.COMPLETED) {
                generateInvoiceIfMissing(updated);
            }

            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in updateServiceOrderStatus() : " + e.getMessage(), e);
            throw e;
        }
    }

    private void generateInvoiceIfMissing(ServiceOrder order) {
        if (invoiceService.invoiceExistsForServiceOrder(order.getId())) {
            log.info("Invoice already exists for service order id: {}, skipping auto-generation", order.getId());
            return;
        }
        InvoiceRequestDTO invoiceRequest = new InvoiceRequestDTO(
                order.getCustomer().getId(),
                order.getId(),
                BigDecimal.ZERO
        );
        invoiceService.createInvoice(invoiceRequest);
        log.info("Invoice auto-generated for completed service order id: {}", order.getId());
    }

    @Override
    public void deleteServiceOrder(Long id) {
        log.info("Execute deleteServiceOrder()");
        try {
            ServiceOrder order = findOrderOrThrow(id);
            serviceOrderRepository.delete(order);
            log.info("Service order deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteServiceOrder() : " + e.getMessage(), e);
            throw e;
        }
    }

    private ServiceOrder findOrderOrThrow(Long id) {
        return serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found with id: " + id));
    }


    @Override
    public List<ServiceOrderResponseDTO> getCompletedServiceOrders() {
        log.info("Execute getCompletedServiceOrders()");
        try {
            return serviceOrderRepository.findByStatusWithDetails(ServiceOrderStatus.COMPLETED)
                    .stream().map(this::toDto).toList();
        } catch (Exception e) {
            log.error("Error in getCompletedServiceOrders() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ServiceOrderResponseDTO> getCustomerServiceHistory(Long customerId) {
        log.info("Execute getCustomerServiceHistory() customerId={}", customerId);
        try {
            if (!customerRepository.existsById(customerId)) {
                throw new ResourceNotFoundException("Customer not found with id: " + customerId);
            }
            return serviceOrderRepository.findServiceHistoryByCustomerId(customerId)
                    .stream().map(this::toDto).toList();
        } catch (Exception e) {
            log.error("Error in getCustomerServiceHistory() : " + e.getMessage(), e);
            throw e;
        }
    }

    private ServiceOrderResponseDTO toDto(ServiceOrder order) {
        List<ServiceOrderItemResponseDTO> itemDtos = order.getItems().stream()
                .map(item -> new ServiceOrderItemResponseDTO(
                        item.getId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal(),
                        item.getService().getId(),
                        item.getService().getName()
                ))
                .toList();

        List<ServiceOrderSparePartResponseDTO> sparePartDtos = order.getSpareParts().stream()
                .map(sp -> new ServiceOrderSparePartResponseDTO(
                        sp.getId(),
                        sp.getQuantity(),
                        sp.getUnitPrice(),
                        sp.getSubtotal(),
                        sp.getSparePart().getId(),
                        sp.getSparePart().getName()
                ))
                .toList();

        return new ServiceOrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getRemarks(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getMotorcycle().getId(),
                order.getMotorcycle().getRegistrationNumber(),
                itemDtos,
                sparePartDtos
        );
    }
}
