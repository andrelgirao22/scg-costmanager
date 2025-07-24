package br.com.alg.scg.domain.sales.entity.repository;

import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    
    /**
     * Busca vendas por cliente
     */
    List<Sale> findByClient(Client client);
    
    /**
     * Busca vendas por período
     */
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Busca vendas por data específica
     */
    List<Sale> findBySaleDate(LocalDate saleDate);
    
    /**
     * Busca vendas ordenadas por data (mais recentes primeiro)
     */
    List<Sale> findAllByOrderBySaleDateDesc();
}
