package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequestDTO(

        @NotBlank(message = "Supplier name is required")
        String name,

        @Email(message = "Email must be a valid address")
        String email,

        String phone
) {
}

