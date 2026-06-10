package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardDTO(
        long totalProducts,
        long totalCategories,
        long totalSuppliers,
        long totalStockUnits,
        BigDecimal totalStockValue,
        long lowStockProducts,
        List<CategoryBreakdownDTO> categoryBreakdown,
        List<LowStockItemDTO> lowStockItems
) {

    public record CategoryBreakdownDTO(
            String name,
            long productCount
    ) {}

    public record LowStockItemDTO(
            Long id,
            String name,
            int quantity,
            String categoryName
    ) {}
}
