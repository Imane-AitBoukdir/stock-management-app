package com.example.backend.service;

import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;
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
@DisplayName("CategoryService — Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category electronics;
    private Category furniture;

    @BeforeEach
    void setUp() {
        electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Electronics");
        electronics.setProducts(new ArrayList<>());

        furniture = new Category();
        furniture.setId(2L);
        furniture.setName("Furniture");
        furniture.setProducts(new ArrayList<>());
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll() — returns all categories")
    void findAll_returnsAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(electronics, furniture));

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Category::getName)
                .containsExactly("Electronics", "Furniture");
    }

    @Test
    @DisplayName("findAll() — returns empty list when no categories")
    void findAll_returnsEmpty_whenNoneExist() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        assertThat(categoryService.findAll()).isEmpty();
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById() — returns category when found")
    void findById_returnsCategory_whenFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));

        Optional<Category> result = categoryService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("findById() — returns empty when not found")
    void findById_returnsEmpty_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(categoryService.findById(99L)).isEmpty();
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save() — persists and returns the category")
    void save_persistsCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(electronics);

        Category result = categoryService.save(electronics);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).save(electronics);
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() — renames an existing category")
    void update_renamesCategory() {
        Category renamed = new Category();
        renamed.setName("Consumer Electronics");
        renamed.setProducts(new ArrayList<>());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Category> result = categoryService.update(1L, renamed);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Consumer Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("update() — returns empty when category not found")
    void update_returnsEmpty_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.update(99L, electronics);

        assertThat(result).isEmpty();
        verify(categoryRepository, never()).save(any());
    }

    // ── deleteById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteById() — deletes and returns true when found")
    void deleteById_returnsTrueAndDeletes_whenFound() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        boolean result = categoryService.deleteById(1L);

        assertThat(result).isTrue();
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById() — returns false without deleting when not found")
    void deleteById_returnsFalse_whenNotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        boolean result = categoryService.deleteById(99L);

        assertThat(result).isFalse();
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
