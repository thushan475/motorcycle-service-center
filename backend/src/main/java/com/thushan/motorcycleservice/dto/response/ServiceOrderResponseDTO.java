package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderResponseDTO {
    private Long id;
    private LocalDate orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String remarks;
    private Long customerId;
    private String customerName;
    private Long motorcycleId;
    private String motorcycleRegistrationNumber;
    private List<ServiceOrderItemResponseDTO> items;
    private List<ServiceOrderSparePartResponseDTO> spareParts;
}
