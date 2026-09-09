package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorcycleRequestDTO {

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Chassis number is required")
    private String chassisNumber;

    @NotBlank(message = "Engine number is required")
    private String engineNumber;

    @NotNull(message = "Year is required")
    @Positive(message = "Year must be a positive number")
    private Integer year;

    private String color;

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Motorcycle brand id is required")
    private Long motorcycleBrandId;
}
