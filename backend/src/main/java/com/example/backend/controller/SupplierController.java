package com.example.backend.controller;

import com.example.backend.dto.SupplierRequestDTO;
import com.example.backend.dto.SupplierResponseDTO;
import com.example.backend.mapper.StockMapper;
import com.example.backend.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final StockMapper stockMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<List<SupplierResponseDTO>> getAll() {
        return ResponseEntity.ok(supplierService.findAll().stream()
                .map(stockMapper::toSupplierResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<SupplierResponseDTO> getById(@PathVariable Long id) {
        return supplierService.findById(id)
                .map(stockMapper::toSupplierResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<SupplierResponseDTO> create(@RequestBody SupplierRequestDTO supplierRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockMapper.toSupplierResponse(supplierService.save(stockMapper.toSupplier(supplierRequest))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<SupplierResponseDTO> update(@PathVariable Long id, @RequestBody SupplierRequestDTO supplierRequest) {
        return supplierService.update(id, stockMapper.toSupplier(supplierRequest))
                .map(stockMapper::toSupplierResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (supplierService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
