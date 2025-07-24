package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.PurchaseService;
import br.com.alg.scg.application.service.SupplierService;
import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import br.com.alg.scg.infra.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    
    private final PurchaseService purchaseService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final DTOMapper mapper;
    
    @Autowired
    public PurchaseController(PurchaseService purchaseService, SupplierService supplierService,
                             ProductService productService, DTOMapper mapper) {
        this.purchaseService = purchaseService;
        this.supplierService = supplierService;
        this.productService = productService;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<PurchaseDTO> createPurchase(@RequestBody CreatePurchaseDTO createDTO) {
        // Validações básicas
        if (createDTO.supplierId() == null || createDTO.purchaseDate() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Supplier supplier = supplierService.findById(createDTO.supplierId())
                .orElse(null);
        if (supplier == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Purchase purchase = purchaseService.createPurchase(supplier, createDTO.purchaseDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(purchase));
    }
    
    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
        List<Purchase> purchases = purchaseService.findAll();
        List<PurchaseDTO> purchaseDTOs = purchases.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(purchaseDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable UUID id) {
        return purchaseService.findById(id)
                .map(purchase -> ResponseEntity.ok(mapper.toDTO(purchase)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable UUID id) {
        if (!purchaseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        purchaseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{purchaseId}/items")
    public ResponseEntity<PurchaseDTO> addItemToPurchase(@PathVariable UUID purchaseId,
                                                        @RequestBody AddPurchaseItemDTO addItemDTO) {
        // Validações básicas
        if (addItemDTO.productId() == null || addItemDTO.quantity() == null || 
            addItemDTO.unitCost() == null || addItemDTO.unitMeasurement() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        if (addItemDTO.quantity().compareTo(java.math.BigDecimal.ZERO) <= 0 ||
            addItemDTO.unitCost().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        Product product = productService.findById(addItemDTO.productId())
                .orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            UnitMeasurement unit = UnitMeasurement.valueOf(addItemDTO.unitMeasurement().toUpperCase());
            Quantity quantity = new Quantity(addItemDTO.quantity(), unit);
            Money unitCost = new Money(addItemDTO.unitCost());
            
            Purchase updatedPurchase = purchaseService.addItem(purchaseId, product, quantity, unitCost);
            return ResponseEntity.ok(mapper.toDTO(updatedPurchase));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getPurchaseCount() {
        return ResponseEntity.ok(purchaseService.count());
    }
    
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseDTO>> getPurchasesBySupplier(@PathVariable UUID supplierId) {
        Supplier supplier = supplierService.findById(supplierId)
                .orElse(null);
        if (supplier == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Purchase> purchases = purchaseService.findBySupplier(supplier);
        List<PurchaseDTO> purchaseDTOs = purchases.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(purchaseDTOs);
    }
}