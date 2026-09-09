package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    @NotNull(message = "Supplier id is required")
    private Long supplierId;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be a positive number")
    private BigDecimal totalAmount;
}
