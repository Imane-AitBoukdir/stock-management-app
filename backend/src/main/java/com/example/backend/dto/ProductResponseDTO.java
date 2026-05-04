package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        CategoryResponseDTO category,
        List<SupplierResponseDTO> suppliers
) {
}
