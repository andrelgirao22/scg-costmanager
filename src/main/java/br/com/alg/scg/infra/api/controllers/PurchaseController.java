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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Compras", description = "Gerenciamento de compras e itens comprados")
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
    @Operation(
        summary = "Criar nova compra",
        description = "Inicia uma nova compra para um fornecedor específico com data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Compra criada com sucesso",
            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Fornecedor não encontrado ou dados inválidos",
            content = @Content
        )
    })
    public ResponseEntity<PurchaseDTO> createPurchase(
            @Parameter(description = "Dados da compra a ser criada", required = true)
            @Valid @RequestBody CreatePurchaseDTO createDTO) {
        Supplier supplier = supplierService.findById(createDTO.supplierId())
                .orElse(null);
        if (supplier == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Purchase purchase = purchaseService.createPurchase(supplier, createDTO.purchaseDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(purchase));
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todas as compras",
        description = "Retorna uma lista com todas as compras realizadas"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de compras retornada com sucesso",
        content = @Content(schema = @Schema(implementation = PurchaseDTO.class))
    )
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
        List<Purchase> purchases = purchaseService.findAll();
        List<PurchaseDTO> purchaseDTOs = purchases.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(purchaseDTOs);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar compra por ID",
        description = "Retorna uma compra específica pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Compra encontrada",
            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Compra não encontrada",
            content = @Content
        )
    })
    public ResponseEntity<PurchaseDTO> getPurchaseById(
            @Parameter(description = "ID único da compra", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return purchaseService.findById(id)
                .map(purchase -> ResponseEntity.ok(mapper.toDTO(purchase)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir compra",
        description = "Remove uma compra do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Compra excluída com sucesso"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Compra não encontrada",
            content = @Content
        )
    })
    public ResponseEntity<Void> deletePurchase(
            @Parameter(description = "ID único da compra", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        if (!purchaseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        purchaseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{purchaseId}/items")
    @Operation(
        summary = "Adicionar item à compra",
        description = "Adiciona um produto com quantidade, unidade e custo unitário a uma compra existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Item adicionado com sucesso",
            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Produto não encontrado, compra não existe ou dados inválidos",
            content = @Content
        )
    })
    public ResponseEntity<PurchaseDTO> addItemToPurchase(
            @Parameter(description = "ID da compra", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID purchaseId,
            @Parameter(description = "Dados do item a ser adicionado", required = true)
            @Valid @RequestBody AddPurchaseItemDTO addItemDTO) {
        
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
    @Operation(
        summary = "Contar compras",
        description = "Retorna o número total de compras realizadas"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Contagem retornada com sucesso",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public ResponseEntity<Long> getPurchaseCount() {
        return ResponseEntity.ok(purchaseService.count());
    }
    
    @GetMapping("/supplier/{supplierId}")
    @Operation(
        summary = "Buscar compras por fornecedor",
        description = "Retorna todas as compras realizadas com um fornecedor específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de compras do fornecedor retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Fornecedor não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<List<PurchaseDTO>> getPurchasesBySupplier(
            @Parameter(description = "ID do fornecedor", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID supplierId) {
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