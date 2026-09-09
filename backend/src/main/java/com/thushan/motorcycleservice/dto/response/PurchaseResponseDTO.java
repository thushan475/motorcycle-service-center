package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDTO {
    private Long id;
    private LocalDate purchaseDate;
    private BigDecimal totalAmount;
    private String status;
    private Long supplierId;
    private String supplierName;
}
