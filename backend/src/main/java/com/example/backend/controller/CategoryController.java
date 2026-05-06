package com.example.backend.controller;

import com.example.backend.dto.CategoryRequestDTO;
import com.example.backend.dto.CategoryResponseDTO;
import com.example.backend.mapper.StockMapper;
import com.example.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final StockMapper stockMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<List<CategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(categoryService.findAll().stream()
                .map(stockMapper::toCategoryResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(stockMapper::toCategoryResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO categoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockMapper.toCategoryResponse(categoryService.save(stockMapper.toCategory(categoryRequest))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody CategoryRequestDTO categoryRequest) {
        return categoryService.update(id, stockMapper.toCategory(categoryRequest))
                .map(stockMapper::toCategoryResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (categoryService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
