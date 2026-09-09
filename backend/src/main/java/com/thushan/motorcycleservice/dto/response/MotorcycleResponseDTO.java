package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorcycleResponseDTO {
    private Long id;
    private String registrationNumber;
    private String chassisNumber;
    private String engineNumber;
    private Integer year;
    private String color;
    private Long customerId;
    private String customerName;
    private Long motorcycleBrandId;
    private String motorcycleBrandName;
}
