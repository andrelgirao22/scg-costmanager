package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.SupplierService;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import br.com.alg.scg.infra.api.dto.CreateSupplierDTO;
import br.com.alg.scg.infra.api.dto.DTOMapper;
import br.com.alg.scg.infra.api.dto.SupplierDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    
    private final SupplierService supplierService;
    private final DTOMapper mapper;
    
    @Autowired
    public SupplierController(SupplierService supplierService, DTOMapper mapper) {
        this.supplierService = supplierService;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody CreateSupplierDTO createDTO) {
        Supplier supplier = supplierService.createSupplier(
                createDTO.name(),
                createDTO.document(),
                mapper.toEntity(createDTO.contact())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(supplier));
    }
    
    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.findAll();
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(supplierDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable UUID id) {
        return supplierService.findById(id)
                .map(supplier -> ResponseEntity.ok(mapper.toDTO(supplier)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        if (!supplierService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getSupplierCount() {
        return ResponseEntity.ok(supplierService.count());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliersByName(@RequestParam String name) {
        List<Supplier> suppliers = supplierService.findByNameContaining(name);
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(supplierDTOs);
    }
}