package br.com.alg.scg.domain.purchases.entity.repository;

import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    
    /**
     * Busca compras por fornecedor
     */
    List<Purchase> findBySupplier(Supplier supplier);
    
    /**
     * Busca compras por período (usando LocalDateTime)
     */
    List<Purchase> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca compras ordenadas por data (mais recentes primeiro)
     */
    List<Purchase> findAllByOrderByDateDesc();
    
    /**
     * Busca todas as compras com fornecedor e itens carregados (fetch join)
     * Para evitar problemas de LazyInitializationException
     */
    @Query("SELECT DISTINCT p FROM Purchase p " +
           "LEFT JOIN FETCH p.supplier " +
           "LEFT JOIN FETCH p.items i " +
           "LEFT JOIN FETCH i.product " +
           "ORDER BY p.date DESC")
    List<Purchase> findAllWithRelations();
}
