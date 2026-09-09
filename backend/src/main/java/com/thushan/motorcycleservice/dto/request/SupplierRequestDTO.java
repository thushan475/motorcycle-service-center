package com.thushan.motorcycleservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String address;
}
