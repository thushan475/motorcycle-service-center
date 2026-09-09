package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartResponseDTO {
    private Long id;
    private String partNumber;
    private String name;
    private String description;
    private BigDecimal sellingPrice;
    private Boolean active;
}