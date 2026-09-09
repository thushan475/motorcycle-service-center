package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequestDTO {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Service order id is required")
    private Long serviceOrderId;

    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discount;
}
