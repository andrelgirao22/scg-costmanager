package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.infra.api.dto.CreateProductDTO;
import br.com.alg.scg.infra.api.dto.DTOMapper;
import br.com.alg.scg.infra.api.dto.ProductDTO;
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
@RequestMapping("/api/products")
@Tag(name = "Produtos", description = "Gerenciamento de produtos e matérias-primas")
public class ProductController {
    
    private final ProductService productService;
    private final DTOMapper mapper;
    
    @Autowired
    public ProductController(ProductService productService, DTOMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }
    
    @PostMapping
    @Operation(
        summary = "Criar novo produto",
        description = "Cria uma nova matéria-prima com nome e estoque inicial"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Produto criado com sucesso",
            content = @Content(schema = @Schema(implementation = ProductDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados inválidos fornecidos",
            content = @Content
        )
    })
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Dados do produto a ser criado", required = true)
            @Valid @RequestBody CreateProductDTO createDTO) {
        Product product = productService.createRawMaterial(createDTO.name(), createDTO.initialStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(product));
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todos os produtos",
        description = "Retorna uma lista com todos os produtos cadastrados"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de produtos retornada com sucesso",
        content = @Content(schema = @Schema(implementation = ProductDTO.class))
    )
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(productDTOs);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar produto por ID",
        description = "Retorna um produto específico pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Produto encontrado",
            content = @Content(schema = @Schema(implementation = ProductDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Produto não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID único do produto", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(mapper.toDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir produto",
        description = "Remove um produto do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Produto excluído com sucesso"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Produto não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID único do produto", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    @Operation(
        summary = "Contar produtos",
        description = "Retorna o número total de produtos cadastrados"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Contagem retornada com sucesso",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.count());
    }
}