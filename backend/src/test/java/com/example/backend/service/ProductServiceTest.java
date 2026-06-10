package com.example.backend.service;

import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.model.Supplier;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;
    private Category sampleCategory;
    private Supplier sampleSupplier;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Electronics");
        sampleCategory.setProducts(new ArrayList<>());

        sampleSupplier = new Supplier();
        sampleSupplier.setId(1L);
        sampleSupplier.setName("TechSupplier");
        sampleSupplier.setEmail("tech@supplier.com");
        sampleSupplier.setProducts(new ArrayList<>());

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Laptop");
        sampleProduct.setDescription("HP EliteBook");
        sampleProduct.setPrice(new BigDecimal("8500.00"));
        sampleProduct.setQuantity(10);
        sampleProduct.setCategory(sampleCategory);
        sampleProduct.setSuppliers(new ArrayList<>(List.of(sampleSupplier)));
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll() — returns all products from repository")
    void findAll_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() — returns empty list when no products exist")
    void findAll_returnsEmptyList_whenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = productService.findAll();

        assertThat(result).isEmpty();
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById() — returns product when it exists")
    void findById_returnsProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Optional<Product> result = productService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() — returns empty Optional when product not found")
    void findById_returnsEmpty_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save() — persists and returns the product")
    void save_persistsProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.findAllById(List.of(1L))).thenReturn(List.of(sampleSupplier));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.save(sampleProduct);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("save() — works when product has no category")
    void save_worksWithoutCategory() {
        sampleProduct.setCategory(null);
        sampleProduct.setSuppliers(new ArrayList<>());
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.save(sampleProduct);

        assertThat(result).isNotNull();
        verify(categoryRepository, never()).findById(anyLong());
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() — updates existing product and returns it")
    void update_updatesExistingProduct() {
        Product updatedData = new Product();
        updatedData.setName("Laptop Pro");
        updatedData.setDescription("Updated description");
        updatedData.setPrice(new BigDecimal("9500.00"));
        updatedData.setQuantity(5);
        updatedData.setCategory(sampleCategory);
        updatedData.setSuppliers(new ArrayList<>());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.findAllById(List.of())).thenReturn(List.of());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Product> result = productService.update(1L, updatedData);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop Pro");
        assertThat(result.get().getPrice()).isEqualByComparingTo("9500.00");
    }

    @Test
    @DisplayName("update() — returns empty Optional when product not found")
    void update_returnsEmpty_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.update(99L, sampleProduct);

        assertThat(result).isEmpty();
        verify(productRepository, never()).save(any());
    }

    // ── deleteById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteById() — returns true and deletes when product exists")
    void deleteById_returnsTrueAndDeletes_whenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean result = productService.deleteById(1L);

        assertThat(result).isTrue();
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById() — returns false and does not delete when not found")
    void deleteById_returnsFalse_whenNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        boolean result = productService.deleteById(99L);

        assertThat(result).isFalse();
        verify(productRepository, never()).deleteById(anyLong());
    }
}
