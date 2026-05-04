package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDTO(
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        Long categoryId,
        List<Long> supplierIds
) {
}
