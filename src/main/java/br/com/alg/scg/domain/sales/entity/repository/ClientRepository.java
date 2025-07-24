package br.com.alg.scg.domain.sales.entity.repository;

import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    
    /**
     * Busca clientes por status
     */
    List<Client> findByStatus(ClientStatus status);
    
    /**
     * Busca clientes ativos
     */
    @Query("SELECT c FROM Client c WHERE c.status = 'ACTIVE'")
    List<Client> findActiveClients();
    
    /**
     * Busca clientes inativos
     */
    @Query("SELECT c FROM Client c WHERE c.status = 'INACTIVE'")
    List<Client> findInactiveClients();
    
    /**
     * Conta clientes por status
     */
    long countByStatus(ClientStatus status);
    
    /**
     * Busca cliente por nome (ignorando case)
     */
    List<Client> findByNameContainingIgnoreCase(String name);
    
    /**
     * Verifica se existe cliente com o nome especificado
     */
    boolean existsByNameIgnoreCase(String name);
}
