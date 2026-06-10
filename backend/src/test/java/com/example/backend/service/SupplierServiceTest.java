package com.example.backend.service;

import com.example.backend.model.Supplier;
import com.example.backend.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService — Unit Tests")
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier techSupplier;
    private Supplier officeSupplier;

    @BeforeEach
    void setUp() {
        techSupplier = new Supplier();
        techSupplier.setId(1L);
        techSupplier.setName("TechSupplier");
        techSupplier.setEmail("tech@supplier.com");
        techSupplier.setPhone("+212600000001");
        techSupplier.setProducts(new ArrayList<>());

        officeSupplier = new Supplier();
        officeSupplier.setId(2L);
        officeSupplier.setName("OfficeWorld");
        officeSupplier.setEmail("office@world.com");
        officeSupplier.setPhone("+212600000002");
        officeSupplier.setProducts(new ArrayList<>());
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll() — returns all suppliers")
    void findAll_returnsAllSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(techSupplier, officeSupplier));

        List<Supplier> result = supplierService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Supplier::getName)
                .containsExactly("TechSupplier", "OfficeWorld");
    }

    @Test
    @DisplayName("findAll() — returns empty list when no suppliers")
    void findAll_returnsEmpty_whenNoneExist() {
        when(supplierRepository.findAll()).thenReturn(List.of());

        assertThat(supplierService.findAll()).isEmpty();
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById() — returns supplier when found")
    void findById_returnsSupplier_whenFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(techSupplier));

        Optional<Supplier> result = supplierService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("tech@supplier.com");
    }

    @Test
    @DisplayName("findById() — returns empty when not found")
    void findById_returnsEmpty_whenNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(supplierService.findById(99L)).isEmpty();
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save() — persists and returns the supplier")
    void save_persistsSupplier() {
        when(supplierRepository.save(any(Supplier.class))).thenReturn(techSupplier);

        Supplier result = supplierService.save(techSupplier);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("TechSupplier");
        verify(supplierRepository, times(1)).save(techSupplier);
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() — updates all fields of an existing supplier")
    void update_updatesAllFields() {
        Supplier updated = new Supplier();
        updated.setName("TechSupplier Pro");
        updated.setEmail("pro@techsupplier.com");
        updated.setPhone("+212699999999");
        updated.setProducts(new ArrayList<>());

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(techSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Supplier> result = supplierService.update(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("TechSupplier Pro");
        assertThat(result.get().getEmail()).isEqualTo("pro@techsupplier.com");
        assertThat(result.get().getPhone()).isEqualTo("+212699999999");
    }

    @Test
    @DisplayName("update() — returns empty when supplier not found")
    void update_returnsEmpty_whenNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Supplier> result = supplierService.update(99L, techSupplier);

        assertThat(result).isEmpty();
        verify(supplierRepository, never()).save(any());
    }

    // ── deleteById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteById() — deletes and returns true when found")
    void deleteById_returnsTrueAndDeletes_whenFound() {
        when(supplierRepository.existsById(1L)).thenReturn(true);

        boolean result = supplierService.deleteById(1L);

        assertThat(result).isTrue();
        verify(supplierRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById() — returns false without deleting when not found")
    void deleteById_returnsFalse_whenNotFound() {
        when(supplierRepository.existsById(99L)).thenReturn(false);

        boolean result = supplierService.deleteById(99L);

        assertThat(result).isFalse();
        verify(supplierRepository, never()).deleteById(anyLong());
    }
}
