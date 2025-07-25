package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.SaleService;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.Sale;
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
@RequestMapping("/api/sales")
@Tag(name = "Vendas", description = "Gerenciamento de vendas e itens vendidos")
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
    @Operation(
        summary = "Criar nova venda",
        description = "Inicia uma nova venda para um cliente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Venda criada com sucesso",
            content = @Content(schema = @Schema(implementation = SaleDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Cliente não encontrado ou dados inválidos",
            content = @Content
        )
    })
    public ResponseEntity<SaleDTO> createSale(
            @Parameter(description = "Dados da venda a ser criada", required = true)
            @Valid @RequestBody CreateSaleDTO createDTO) {
        Client client = clientService.findById(createDTO.clientId())
                .orElse(null);
        if (client == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Sale sale = saleService.createSale(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(sale));
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todas as vendas",
        description = "Retorna uma lista com todas as vendas realizadas"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de vendas retornada com sucesso",
        content = @Content(schema = @Schema(implementation = SaleDTO.class))
    )
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<Sale> sales = saleService.findAll();
        List<SaleDTO> saleDTOs = sales.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(saleDTOs);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar venda por ID",
        description = "Retorna uma venda específica pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Venda encontrada",
            content = @Content(schema = @Schema(implementation = SaleDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Venda não encontrada",
            content = @Content
        )
    })
    public ResponseEntity<SaleDTO> getSaleById(
            @Parameter(description = "ID único da venda", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return saleService.findById(id)
                .map(sale -> ResponseEntity.ok(mapper.toDTO(sale)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir venda",
        description = "Remove uma venda do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Venda excluída com sucesso"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Venda não encontrada",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteSale(
            @Parameter(description = "ID único da venda", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        if (!saleService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        saleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{saleId}/items")
    @Operation(
        summary = "Adicionar item à venda",
        description = "Adiciona um produto com quantidade específica a uma venda existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Item adicionado com sucesso",
            content = @Content(schema = @Schema(implementation = SaleDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Produto não encontrado, venda não existe ou dados inválidos",
            content = @Content
        )
    })
    public ResponseEntity<SaleDTO> addItemToSale(
            @Parameter(description = "ID da venda", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID saleId,
            @Parameter(description = "Dados do item a ser adicionado", required = true)
            @Valid @RequestBody AddSaleItemDTO addItemDTO) {
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
    @Operation(
        summary = "Contar vendas",
        description = "Retorna o número total de vendas realizadas"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Contagem retornada com sucesso",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public ResponseEntity<Long> getSaleCount() {
        return ResponseEntity.ok(saleService.count());
    }
    
    @GetMapping("/client/{clientId}")
    @Operation(
        summary = "Buscar vendas por cliente",
        description = "Retorna todas as vendas realizadas para um cliente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de vendas do cliente retornada com sucesso",
            content = @Content(schema = @Schema(implementation = SaleDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Cliente não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<List<SaleDTO>> getSalesByClient(
            @Parameter(description = "ID do cliente", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID clientId) {
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