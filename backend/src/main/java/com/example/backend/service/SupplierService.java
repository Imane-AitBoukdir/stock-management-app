package com.example.backend.service;

import com.example.backend.model.Supplier;
import com.example.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }

    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> update(Long id, Supplier updated) {
        return supplierRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setEmail(updated.getEmail());
            existing.setPhone(updated.getPhone());
            return supplierRepository.save(existing);
        });
    }

    public boolean deleteById(Long id) {
        if (!supplierRepository.existsById(id)) {
            return false;
        }
        supplierRepository.deleteById(id);
        return true;
    }
}
