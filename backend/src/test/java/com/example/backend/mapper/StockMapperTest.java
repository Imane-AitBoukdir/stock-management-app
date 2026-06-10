package com.example.backend.mapper;

import com.example.backend.dto.*;
import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StockMapper — Unit Tests")
class StockMapperTest {

    private StockMapper stockMapper;

    @BeforeEach
    void setUp() {
        stockMapper = new StockMapper();
    }

    // ── Category mapping ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Category mapping")
    class CategoryMapping {

        @Test
        @DisplayName("toCategory() — maps DTO name to Category entity")
        void toCategory_mapsDtoToEntity() {
            CategoryRequestDTO dto = new CategoryRequestDTO("Electronics");

            Category category = stockMapper.toCategory(dto);

            assertThat(category.getName()).isEqualTo("Electronics");
            assertThat(category.getId()).isNull();
        }

        @Test
        @DisplayName("toCategoryResponse() — maps Category entity to response DTO")
        void toCategoryResponse_mapsEntityToDto() {
            Category category = new Category();
            category.setId(1L);
            category.setName("Electronics");

            CategoryResponseDTO response = stockMapper.toCategoryResponse(category);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("toCategoryResponse() — returns null when category is null")
        void toCategoryResponse_returnsNull_whenCategoryIsNull() {
            assertThat(stockMapper.toCategoryResponse(null)).isNull();
        }
    }

    // ── Supplier mapping ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Supplier mapping")
    class SupplierMapping {

        @Test
        @DisplayName("toSupplier() — maps DTO fields to Supplier entity")
        void toSupplier_mapsDtoToEntity() {
            SupplierRequestDTO dto = new SupplierRequestDTO("TechSupplier", "tech@supplier.com", "+212600000001");

            Supplier supplier = stockMapper.toSupplier(dto);

            assertThat(supplier.getName()).isEqualTo("TechSupplier");
            assertThat(supplier.getEmail()).isEqualTo("tech@supplier.com");
            assertThat(supplier.getPhone()).isEqualTo("+212600000001");
            assertThat(supplier.getId()).isNull();
        }

        @Test
        @DisplayName("toSupplierResponse() — maps Supplier entity to response DTO")
        void toSupplierResponse_mapsEntityToDto() {
            Supplier supplier = new Supplier();
            supplier.setId(1L);
            supplier.setName("TechSupplier");
            supplier.setEmail("tech@supplier.com");
            supplier.setPhone("+212600000001");

            SupplierResponseDTO response = stockMapper.toSupplierResponse(supplier);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("TechSupplier");
            assertThat(response.email()).isEqualTo("tech@supplier.com");
            assertThat(response.phone()).isEqualTo("+212600000001");
        }

        @Test
        @DisplayName("toSupplierResponse() — returns null when supplier is null")
        void toSupplierResponse_returnsNull_whenSupplierIsNull() {
            assertThat(stockMapper.toSupplierResponse(null)).isNull();
        }
    }

    // ── Product mapping ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Product mapping")
    class ProductMapping {

        @Test
        @DisplayName("toProduct() — maps all DTO fields to Product entity")
        void toProduct_mapsAllFields() {
            ProductRequestDTO dto = new ProductRequestDTO(
                    "Laptop", "HP EliteBook",
                    new BigDecimal("8500.00"), 10,
                    1L, List.of(1L, 2L)
            );

            Product product = stockMapper.toProduct(dto);

            assertThat(product.getName()).isEqualTo("Laptop");
            assertThat(product.getDescription()).isEqualTo("HP EliteBook");
            assertThat(product.getPrice()).isEqualByComparingTo("8500.00");
            assertThat(product.getQuantity()).isEqualTo(10);
            assertThat(product.getCategory()).isNotNull();
            assertThat(product.getCategory().getId()).isEqualTo(1L);
            assertThat(product.getSuppliers()).hasSize(2);
            assertThat(product.getSuppliers()).extracting(Supplier::getId)
                    .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("toProduct() — handles null categoryId gracefully")
        void toProduct_handlesNullCategoryId() {
            ProductRequestDTO dto = new ProductRequestDTO(
                    "Keyboard", "Mechanical", new BigDecimal("650.00"), 20,
                    null, List.of(1L)
            );

            Product product = stockMapper.toProduct(dto);

            assertThat(product.getCategory()).isNull();
        }

        @Test
        @DisplayName("toProduct() — handles null supplierIds gracefully")
        void toProduct_handlesNullSupplierIds() {
            ProductRequestDTO dto = new ProductRequestDTO(
                    "Mouse", "Wireless", new BigDecimal("200.00"), 50,
                    1L, null
            );

            Product product = stockMapper.toProduct(dto);

            // Suppliers list should be the default from entity (not overwritten)
            // or null depending on DTO handling
            assertThat(product.getSuppliers()).isNotNull();
        }

        @Test
        @DisplayName("toProduct() — handles empty supplierIds list")
        void toProduct_handlesEmptySupplierIds() {
            ProductRequestDTO dto = new ProductRequestDTO(
                    "Mouse", "Wireless", new BigDecimal("200.00"), 50,
                    1L, List.of()
            );

            Product product = stockMapper.toProduct(dto);

            assertThat(product.getSuppliers()).isEmpty();
        }

        @Test
        @DisplayName("toProductResponse() — maps Product entity to full response DTO")
        void toProductResponse_mapsEntityToDto() {
            Category category = new Category();
            category.setId(1L);
            category.setName("Electronics");

            Supplier supplier = new Supplier();
            supplier.setId(1L);
            supplier.setName("TechSupplier");
            supplier.setEmail("tech@supplier.com");
            supplier.setPhone("+212600000001");

            Product product = new Product();
            product.setId(1L);
            product.setName("Laptop");
            product.setDescription("HP EliteBook");
            product.setPrice(new BigDecimal("8500.00"));
            product.setQuantity(10);
            product.setCategory(category);
            product.setSuppliers(new ArrayList<>(List.of(supplier)));

            ProductResponseDTO response = stockMapper.toProductResponse(product);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Laptop");
            assertThat(response.description()).isEqualTo("HP EliteBook");
            assertThat(response.price()).isEqualByComparingTo("8500.00");
            assertThat(response.quantity()).isEqualTo(10);
            assertThat(response.category()).isNotNull();
            assertThat(response.category().name()).isEqualTo("Electronics");
            assertThat(response.suppliers()).hasSize(1);
            assertThat(response.suppliers().get(0).name()).isEqualTo("TechSupplier");
        }

        @Test
        @DisplayName("toProductResponse() — handles null category in product")
        void toProductResponse_handlesNullCategory() {
            Product product = new Product();
            product.setId(1L);
            product.setName("Keyboard");
            product.setPrice(new BigDecimal("650.00"));
            product.setQuantity(20);
            product.setCategory(null);
            product.setSuppliers(new ArrayList<>());

            ProductResponseDTO response = stockMapper.toProductResponse(product);

            assertThat(response.category()).isNull();
            assertThat(response.suppliers()).isEmpty();
        }

        @Test
        @DisplayName("toProductResponse() — handles null suppliers list in product")
        void toProductResponse_handlesNullSuppliers() {
            Product product = new Product();
            product.setId(1L);
            product.setName("Keyboard");
            product.setPrice(new BigDecimal("650.00"));
            product.setQuantity(20);
            product.setCategory(null);
            product.setSuppliers(null);

            ProductResponseDTO response = stockMapper.toProductResponse(product);

            assertThat(response.suppliers()).isEmpty();
        }
    }
}
