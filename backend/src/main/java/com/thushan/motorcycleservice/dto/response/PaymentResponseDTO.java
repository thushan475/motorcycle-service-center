package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
    private Long invoiceId;
    private String invoiceNumber;
}
