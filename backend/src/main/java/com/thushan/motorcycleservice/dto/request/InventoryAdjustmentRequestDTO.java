package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAdjustmentRequestDTO {

    @NotNull(message = "Change amount is required")
    private Integer change;
}
