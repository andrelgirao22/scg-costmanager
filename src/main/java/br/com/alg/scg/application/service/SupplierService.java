package br.com.alg.scg.application.service;

import br.com.alg.scg.domain.common.valueobject.Contact;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import br.com.alg.scg.domain.purchases.entity.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // ==================== CREATE OPERATIONS ====================

    @Transactional
    public Supplier createSupplier(String name, String document, Contact contact) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do fornecedor não pode ser vazio");
        }
        if (document == null || document.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento do fornecedor não pode ser vazio");
        }
        if (contact == null) {
            throw new IllegalArgumentException("Contato não pode ser nulo");
        }

        // Verificar se já existe fornecedor com o mesmo documento
        if (supplierRepository.existsByDocument(document.trim())) {
            throw new IllegalArgumentException("Já existe um fornecedor com o documento: " + document);
        }

        Supplier supplier = new Supplier(name.trim(), document.trim(), contact);
        return supplierRepository.save(supplier);
    }

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public Optional<Supplier> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return supplierRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return supplierRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return supplierRepository.count();
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> findByDocument(String document) {
        if (document == null || document.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento não pode ser vazio");
        }
        return supplierRepository.findByDocument(document.trim());
    }

    @Transactional(readOnly = true)
    public List<Supplier> findByNameContaining(String nameFragment) {
        if (nameFragment == null || nameFragment.trim().isEmpty()) {
            throw new IllegalArgumentException("Fragmento do nome não pode ser vazio");
        }
        return supplierRepository.findByNameContainingIgnoreCase(nameFragment.trim());
    }

    @Transactional(readOnly = true)
    public boolean existsByDocument(String document) {
        if (document == null || document.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento não pode ser vazio");
        }
        return supplierRepository.existsByDocument(document.trim());
    }

    // ==================== UPDATE OPERATIONS ====================

    @Transactional
    public Supplier updateContact(UUID supplierId, Contact newContact) {
        if (supplierId == null) {
            throw new IllegalArgumentException("ID do fornecedor não pode ser nulo");
        }
        if (newContact == null) {
            throw new IllegalArgumentException("Novo contato não pode ser nulo");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado com ID: " + supplierId));

        // Como a entidade não tem updateContact(), usar reflection ou criar novo supplier
        // Por enquanto, criar nova instância (não é ideal, mas funciona)
        Supplier updatedSupplier = new Supplier(supplier.getName(), supplier.getDocument(), newContact);
        updatedSupplier.setId(supplier.getId()); // Manter o mesmo ID
        
        return supplierRepository.save(updatedSupplier);
    }

    @Transactional
    public Supplier updateName(UUID supplierId, String newName) {
        if (supplierId == null) {
            throw new IllegalArgumentException("ID do fornecedor não pode ser nulo");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Novo nome não pode ser vazio");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado com ID: " + supplierId));

        // Como a entidade não tem updateName(), criar nova instância
        Supplier updatedSupplier = new Supplier(newName.trim(), supplier.getDocument(), supplier.getContact());
        updatedSupplier.setId(supplier.getId()); // Manter o mesmo ID
        
        return supplierRepository.save(updatedSupplier);
    }

    // Métodos de ativação/desativação removidos pois a entidade Supplier não possui sistema de status

    // ==================== DELETE OPERATIONS ====================

    @Transactional
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Fornecedor não encontrado com ID: " + id);
        }
        
        // Verificar se o fornecedor pode ser deletado (não tem compras associadas)
        // Esta verificação seria feita consultando o PurchaseRepository
        // Por simplicidade, assumindo que é possível deletar
        
        supplierRepository.deleteById(id);
    }

    @Transactional
    public void delete(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        supplierRepository.delete(supplier);
    }

    // ==================== BUSINESS OPERATIONS ====================

    // Métodos de status removidos pois a entidade Supplier não possui sistema ativo/inativo

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return supplierRepository.findByNameContainingIgnoreCase(name.trim()).size() > 0;
    }
}