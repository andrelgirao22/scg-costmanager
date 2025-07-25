package br.com.alg.scg.application.service;

import br.com.alg.scg.domain.common.valueobject.Address;
import br.com.alg.scg.domain.common.valueobject.Contact;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.ClientStatus;
import br.com.alg.scg.domain.sales.entity.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // ==================== CREATE OPERATIONS ====================

    @Transactional
    public Client createClient(String name, Contact contact, Address address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        }
        if (contact == null) {
            throw new IllegalArgumentException("Contato não pode ser nulo");
        }
        if (address == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo");
        }

        Client client = new Client(name, contact, address);
        return clientRepository.save(client);
    }

    @Transactional
    public Client save(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        
        // Validações básicas
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        }
        if (client.getContact() == null) {
            throw new IllegalArgumentException("Contato não pode ser nulo");
        }
        
        return clientRepository.save(client);
    }

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public Optional<Client> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return clientRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return clientRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return clientRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Client> findByStatus(ClientStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
        return clientRepository.findByStatus(status);
    }

    // ==================== UPDATE OPERATIONS ====================

    @Transactional
    public Client updateContact(UUID clientId, Contact newContact) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        if (newContact == null) {
            throw new IllegalArgumentException("Novo contato não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        client.updateContact(newContact);
        return clientRepository.save(client);
    }

    @Transactional
    public Client updateAddress(UUID clientId, Address newAddress) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        if (newAddress == null) {
            throw new IllegalArgumentException("Novo endereço não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        client.changeDeliveryAddress(newAddress);
        return clientRepository.save(client);
    }

    @Transactional
    public Client blockClient(UUID clientId, String reason) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo do bloqueio não pode ser vazio");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        client.block(reason.trim());
        return clientRepository.save(client);
    }

    @Transactional
    public Client unblockClient(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        client.unblock();
        return clientRepository.save(client);
    }

    @Transactional
    public Client deactivateClient(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        client.deactivate();
        return clientRepository.save(client);
    }

    // ==================== DELETE OPERATIONS ====================

    @Transactional
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado com ID: " + id);
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public void delete(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        clientRepository.delete(client);
    }

    // ==================== BUSINESS OPERATIONS ====================

    @Transactional(readOnly = true)
    public boolean isActive(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        return client.getStatus() == ClientStatus.ACTIVE;
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        return client.getStatus() == ClientStatus.BLOCKED;
    }

    @Transactional(readOnly = true)
    public boolean isInactive(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clientId));

        return client.getStatus() == ClientStatus.INACTIVE;
    }

    @Transactional(readOnly = true)
    public long countActiveClients() {
        return clientRepository.countByStatus(ClientStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public long countInactiveClients() {
        return clientRepository.countByStatus(ClientStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    public long countBlockedClients() {
        return clientRepository.countByStatus(ClientStatus.BLOCKED);
    }

    @Transactional(readOnly = true)
    public List<Client> findActiveClients() {
        return clientRepository.findActiveClients();
    }

    @Transactional(readOnly = true)
    public List<Client> findInactiveClients() {
        return clientRepository.findInactiveClients();
    }

    @Transactional(readOnly = true)
    public List<Client> findBlockedClients() {
        return clientRepository.findByStatus(ClientStatus.BLOCKED);
    }

    @Transactional(readOnly = true)
    public List<Client> findByNameContaining(String nameFragment) {
        if (nameFragment == null || nameFragment.trim().isEmpty()) {
            throw new IllegalArgumentException("Fragmento do nome não pode ser vazio");
        }
        return clientRepository.findByNameContainingIgnoreCase(nameFragment.trim());
    }


    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return clientRepository.existsByNameIgnoreCase(name.trim());
    }
}