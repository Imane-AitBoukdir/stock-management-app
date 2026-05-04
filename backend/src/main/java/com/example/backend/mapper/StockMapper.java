package com.example.backend.mapper;

import com.example.backend.dto.*;
import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.model.Supplier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockMapper {

    public Category toCategory(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.name());
        return category;
    }

    public CategoryResponseDTO toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    public Supplier toSupplier(SupplierRequestDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setName(dto.name());
        supplier.setEmail(dto.email());
        supplier.setPhone(dto.phone());
        return supplier;
    }

    public SupplierResponseDTO toSupplierResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new SupplierResponseDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone()
        );
    }

    public Product toProduct(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());

        if (dto.categoryId() != null) {
            Category category = new Category();
            category.setId(dto.categoryId());
            product.setCategory(category);
        }

        if (dto.supplierIds() != null) {
            List<Supplier> suppliers = dto.supplierIds().stream()
                    .map(id -> {
                        Supplier supplier = new Supplier();
                        supplier.setId(id);
                        return supplier;
                    })
                    .toList();
            product.setSuppliers(new ArrayList<>(suppliers));
        }

        return product;
    }

    public ProductResponseDTO toProductResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                toCategoryResponse(product.getCategory()),
                product.getSuppliers() == null
                        ? List.of()
                        : product.getSuppliers().stream().map(this::toSupplierResponse).toList()
        );
    }
}
