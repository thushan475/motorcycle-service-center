package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderRequestDTO {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Motorcycle id is required")
    private Long motorcycleId;

    private String remarks;

    @NotEmpty(message = "A service order must contain at least one service item")
    @Valid
    private List<ServiceOrderItemRequestDTO> items;

    @Valid
    private List<ServiceOrderSparePartRequestDTO> spareParts;
}
