package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.SaleService;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.Sale;
import br.com.alg.scg.infra.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
    
    private final SaleService saleService;
    private final ClientService clientService;
    private final ProductService productService;
    private final DTOMapper mapper;
    
    @Autowired
    public SaleController(SaleService saleService, ClientService clientService, 
                         ProductService productService, DTOMapper mapper) {
        this.saleService = saleService;
        this.clientService = clientService;
        this.productService = productService;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<SaleDTO> createSale(@RequestBody CreateSaleDTO createDTO) {
        // Validações básicas
        if (createDTO.clientId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Client client = clientService.findById(createDTO.clientId())
                .orElse(null);
        if (client == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Sale sale = saleService.createSale(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(sale));
    }
    
    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<Sale> sales = saleService.findAll();
        List<SaleDTO> saleDTOs = sales.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(saleDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable UUID id) {
        return saleService.findById(id)
                .map(sale -> ResponseEntity.ok(mapper.toDTO(sale)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        if (!saleService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        saleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{saleId}/items")
    public ResponseEntity<SaleDTO> addItemToSale(@PathVariable UUID saleId, 
                                                 @RequestBody AddSaleItemDTO addItemDTO) {
        // Validações básicas
        if (addItemDTO.productId() == null || addItemDTO.quantity() == null || addItemDTO.quantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        Product product = productService.findById(addItemDTO.productId())
                .orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Sale updatedSale = saleService.addItem(saleId, product, addItemDTO.quantity());
            return ResponseEntity.ok(mapper.toDTO(updatedSale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getSaleCount() {
        return ResponseEntity.ok(saleService.count());
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SaleDTO>> getSalesByClient(@PathVariable UUID clientId) {
        Client client = clientService.findById(clientId)
                .orElse(null);
        if (client == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Sale> sales = saleService.findByClient(client);
        List<SaleDTO> saleDTOs = sales.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(saleDTOs);
    }
}