package com.example.backend.controller;

import com.example.backend.dto.ProductRequestDTO;
import com.example.backend.dto.ProductResponseDTO;
import com.example.backend.mapper.StockMapper;
import com.example.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.backend.model.Product;
import jakarta.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final StockMapper stockMapper;

    // GET /api/products — paginated, filtered, sorted list
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<Page<ProductResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        Sort sortObj = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
        if (name != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (category != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("name"), category));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.ge(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.le(root.get("price"), maxPrice));
        }

        Page<ProductResponseDTO> pageRes = productService.findAll(spec, pageable)
                .map(stockMapper::toProductResponse);
        return ResponseEntity.ok(pageRes);
    }

    // GET /api/products/{id} — get one
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(stockMapper::toProductResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/products — create
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO created = stockMapper.toProductResponse(
                productService.save(stockMapper.toProduct(productRequest))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/products/{id} — update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequest
    ) {
        return productService.update(id, stockMapper.toProduct(productRequest))
                .map(stockMapper::toProductResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/products/{id} — delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}