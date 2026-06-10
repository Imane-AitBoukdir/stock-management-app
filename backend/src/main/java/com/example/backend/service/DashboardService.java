package com.example.backend.service;

import com.example.backend.dto.DashboardDTO;
import com.example.backend.dto.DashboardDTO.CategoryBreakdownDTO;
import com.example.backend.dto.DashboardDTO.LowStockItemDTO;
import com.example.backend.model.Product;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public DashboardDTO getDashboard() {
        List<Product> products = productRepository.findAll();

        long totalProducts = products.size();
        long totalCategories = categoryRepository.count();
        long totalSuppliers = supplierRepository.count();

        long totalStockUnits = products.stream()
                .mapToLong(Product::getQuantity)
                .sum();

        BigDecimal totalStockValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Product> lowStock = products.stream()
                .filter(p -> p.getQuantity() <= LOW_STOCK_THRESHOLD)
                .sorted(Comparator.comparingInt(Product::getQuantity))
                .toList();

        List<CategoryBreakdownDTO> categoryBreakdown = products.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory().getName() : "Uncategorized",
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> new CategoryBreakdownDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(CategoryBreakdownDTO::productCount).reversed())
                .toList();

        List<LowStockItemDTO> lowStockItems = lowStock.stream()
                .map(p -> new LowStockItemDTO(
                        p.getId(),
                        p.getName(),
                        p.getQuantity(),
                        p.getCategory() != null ? p.getCategory().getName() : null
                ))
                .toList();

        return new DashboardDTO(
                totalProducts,
                totalCategories,
                totalSuppliers,
                totalStockUnits,
                totalStockValue,
                lowStock.size(),
                categoryBreakdown,
                lowStockItems
        );
    }
}
