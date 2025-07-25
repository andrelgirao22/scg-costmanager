package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.SupplierService;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import br.com.alg.scg.infra.api.dto.CreateSupplierDTO;
import br.com.alg.scg.infra.api.dto.DTOMapper;
import br.com.alg.scg.infra.api.dto.SupplierDTO;
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
@RequestMapping("/api/suppliers")
@Tag(name = "Fornecedores", description = "Gerenciamento de fornecedores e relacionamentos comerciais")
public class SupplierController {
    
    private final SupplierService supplierService;
    private final DTOMapper mapper;
    
    @Autowired
    public SupplierController(SupplierService supplierService, DTOMapper mapper) {
        this.supplierService = supplierService;
        this.mapper = mapper;
    }
    
    @PostMapping
    @Operation(
        summary = "Criar novo fornecedor",
        description = "Cadastra um novo fornecedor com CNPJ e dados de contato"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Fornecedor criado com sucesso",
            content = @Content(schema = @Schema(implementation = SupplierDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados inválidos fornecidos",
            content = @Content
        )
    })
    public ResponseEntity<SupplierDTO> createSupplier(
            @Parameter(description = "Dados do fornecedor a ser criado", required = true)
            @Valid @RequestBody CreateSupplierDTO createDTO) {
        Supplier supplier = supplierService.createSupplier(
                createDTO.name(),
                createDTO.document(),
                mapper.toEntity(createDTO.contact())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(supplier));
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todos os fornecedores",
        description = "Retorna uma lista com todos os fornecedores cadastrados"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de fornecedores retornada com sucesso",
        content = @Content(schema = @Schema(implementation = SupplierDTO.class))
    )
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.findAll();
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(supplierDTOs);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar fornecedor por ID",
        description = "Retorna um fornecedor específico pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Fornecedor encontrado",
            content = @Content(schema = @Schema(implementation = SupplierDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Fornecedor não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<SupplierDTO> getSupplierById(
            @Parameter(description = "ID único do fornecedor", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return supplierService.findById(id)
                .map(supplier -> ResponseEntity.ok(mapper.toDTO(supplier)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir fornecedor",
        description = "Remove um fornecedor do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Fornecedor excluído com sucesso"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Fornecedor não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "ID único do fornecedor", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        if (!supplierService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    @Operation(
        summary = "Contar fornecedores",
        description = "Retorna o número total de fornecedores cadastrados"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Contagem retornada com sucesso",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public ResponseEntity<Long> getSupplierCount() {
        return ResponseEntity.ok(supplierService.count());
    }
    
    @GetMapping("/search")
    @Operation(
        summary = "Buscar fornecedores por nome",
        description = "Procura fornecedores que contenham o texto especificado no nome"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de fornecedores encontrados",
        content = @Content(schema = @Schema(implementation = SupplierDTO.class))
    )
    public ResponseEntity<List<SupplierDTO>> searchSuppliersByName(
            @Parameter(description = "Texto para busca no nome do fornecedor", required = true, example = "ABC Distribuidora")
            @RequestParam String name) {
        List<Supplier> suppliers = supplierService.findByNameContaining(name);
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(supplierDTOs);
    }
}