package com.example.backend.dto;

public record SupplierResponseDTO(
        Long id,
        String name,
        String email,
        String phone
) {
}
