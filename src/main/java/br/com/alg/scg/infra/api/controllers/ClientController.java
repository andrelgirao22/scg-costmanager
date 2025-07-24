package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.infra.api.dto.ClientDTO;
import br.com.alg.scg.infra.api.dto.CreateClientDTO;
import br.com.alg.scg.infra.api.dto.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    
    private final ClientService clientService;
    private final DTOMapper mapper;
    
    @Autowired
    public ClientController(ClientService clientService, DTOMapper mapper) {
        this.clientService = clientService;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody CreateClientDTO createDTO) {
        Client client = clientService.createClient(
                createDTO.name(),
                mapper.toEntity(createDTO.contact()),
                mapper.toEntity(createDTO.deliveryAddress())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(client));
    }
    
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<Client> clients = clientService.findAll();
        List<ClientDTO> clientDTOs = clients.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(clientDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable UUID id) {
        return clientService.findById(id)
                .map(client -> ResponseEntity.ok(mapper.toDTO(client)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        if (!clientService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getClientCount() {
        return ResponseEntity.ok(clientService.count());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<ClientDTO>> getActiveClients() {
        List<Client> activeClients = clientService.findActiveClients();
        List<ClientDTO> clientDTOs = activeClients.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(clientDTOs);
    }
}