package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.infra.api.dto.ClientDTO;
import br.com.alg.scg.infra.api.dto.CreateClientDTO;
import br.com.alg.scg.infra.api.dto.DTOMapper;
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
@RequestMapping("/api/clients")
@Tag(name = "Clientes", description = "Gerenciamento de clientes e relacionamentos comerciais")
public class ClientController {
    
    private final ClientService clientService;
    private final DTOMapper mapper;
    
    @Autowired
    public ClientController(ClientService clientService, DTOMapper mapper) {
        this.clientService = clientService;
        this.mapper = mapper;
    }
    
    @PostMapping
    @Operation(
        summary = "Criar novo cliente",
        description = "Cadastra um novo cliente com dados de contato e endereço de entrega"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Cliente criado com sucesso",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados inválidos fornecidos",
            content = @Content
        )
    })
    public ResponseEntity<ClientDTO> createClient(
            @Parameter(description = "Dados do cliente a ser criado", required = true)
            @Valid @RequestBody CreateClientDTO createDTO) {
        Client client = clientService.createClient(
                createDTO.name(),
                mapper.toEntity(createDTO.contact()),
                mapper.toEntity(createDTO.deliveryAddress())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(client));
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todos os clientes",
        description = "Retorna uma lista com todos os clientes cadastrados"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de clientes retornada com sucesso",
        content = @Content(schema = @Schema(implementation = ClientDTO.class))
    )
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<Client> clients = clientService.findAll();
        List<ClientDTO> clientDTOs = clients.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(clientDTOs);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar cliente por ID",
        description = "Retorna um cliente específico pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Cliente não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<ClientDTO> getClientById(
            @Parameter(description = "ID único do cliente", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return clientService.findById(id)
                .map(client -> ResponseEntity.ok(mapper.toDTO(client)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir cliente",
        description = "Remove um cliente do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Cliente excluído com sucesso"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Cliente não encontrado",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "ID único do cliente", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        if (!clientService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    @Operation(
        summary = "Contar clientes",
        description = "Retorna o número total de clientes cadastrados"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Contagem retornada com sucesso",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public ResponseEntity<Long> getClientCount() {
        return ResponseEntity.ok(clientService.count());
    }
    
    @GetMapping("/active")
    @Operation(
        summary = "Listar clientes ativos",
        description = "Retorna apenas os clientes com status ativo no sistema"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de clientes ativos retornada com sucesso",
        content = @Content(schema = @Schema(implementation = ClientDTO.class))
    )
    public ResponseEntity<List<ClientDTO>> getActiveClients() {
        List<Client> activeClients = clientService.findActiveClients();
        List<ClientDTO> clientDTOs = activeClients.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(clientDTOs);
    }
}