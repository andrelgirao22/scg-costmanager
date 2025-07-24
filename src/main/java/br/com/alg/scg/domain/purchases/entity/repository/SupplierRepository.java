package br.com.alg.scg.domain.purchases.entity.repository;

import br.com.alg.scg.domain.purchases.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    
    /**
     * Busca fornecedor por nome exato (ignorando case)
     */
    Optional<Supplier> findByName(String name);
    
    /**
     * Busca fornecedores por fragmento do nome (ignorando case)
     */
    List<Supplier> findByNameContainingIgnoreCase(String nameFragment);
    
    /**
     * Busca fornecedor por documento
     */
    Optional<Supplier> findByDocument(String document);
    
    /**
     * Verifica se existe fornecedor com o documento especificado
     */
    boolean existsByDocument(String document);
    
    /**
     * Verifica se existe fornecedor com o nome especificado
     */
    boolean existsByNameIgnoreCase(String name);
}
