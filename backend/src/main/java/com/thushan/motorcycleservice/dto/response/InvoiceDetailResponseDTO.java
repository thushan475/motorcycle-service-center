package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDetailResponseDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String status;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private Long serviceOrderId;
    private LocalDate orderDate;
    private String motorcycleRegistration;
    private String motorcycleBrand;
    private String motorcycleModelYear;
    private String motorcycleColor;
    private List<ServiceOrderItemResponseDTO> services;
    private List<ServiceOrderSparePartResponseDTO> spareParts;
    private List<PaymentResponseDTO> payments;
    private BigDecimal totalPaid;
    private BigDecimal balance;
}
