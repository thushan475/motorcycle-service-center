package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartRequestDTO {

    @NotBlank(message = "Part number is required")
    private String partNumber;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be a positive number")
    private BigDecimal sellingPrice;

    private Boolean active;
}