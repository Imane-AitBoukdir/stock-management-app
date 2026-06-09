package com.example.backend.service;

import com.example.backend.model.Product;
import com.example.backend.model.Supplier;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        attachRelations(product);
        return productRepository.save(product);
    }

    public Optional<Product> update(Long id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setQuantity(updated.getQuantity());
            existing.setCategory(updated.getCategory());
            existing.setSuppliers(updated.getSuppliers());
            attachRelations(existing);
            return productRepository.save(existing);
        });
    }

    public boolean deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    private void attachRelations(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryRepository.findById(product.getCategory().getId())
                    .ifPresent(product::setCategory);
        }

        if (product.getSuppliers() != null) {
            List<Long> supplierIds = product.getSuppliers().stream()
                    .map(Supplier::getId)
                    .filter(Objects::nonNull)
                    .toList();
            product.setSuppliers(supplierRepository.findAllById(supplierIds));
        }
    }
}
