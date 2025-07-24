package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.infra.api.dto.CreateProductDTO;
import br.com.alg.scg.infra.api.dto.DTOMapper;
import br.com.alg.scg.infra.api.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    private final DTOMapper mapper;
    
    @Autowired
    public ProductController(ProductService productService, DTOMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductDTO createDTO) {
        // Validações básicas
        if (createDTO.name() == null || createDTO.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (createDTO.initialStock() == null || createDTO.initialStock().compareTo(java.math.BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().build();
        }
        
        Product product = productService.createRawMaterial(createDTO.name(), createDTO.initialStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(product));
    }
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(productDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(mapper.toDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.count());
    }
}