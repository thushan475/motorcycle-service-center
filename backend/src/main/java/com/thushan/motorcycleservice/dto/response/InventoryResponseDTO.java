package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
    private Long id;
    private Integer quantity;
    private LocalDateTime lastUpdated;
    private Long sparePartId;
    private String sparePartName;
    private String partNumber;
}
