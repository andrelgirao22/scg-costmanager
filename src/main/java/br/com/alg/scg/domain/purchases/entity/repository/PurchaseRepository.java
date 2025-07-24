package br.com.alg.scg.domain.purchases.entity.repository;

import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
