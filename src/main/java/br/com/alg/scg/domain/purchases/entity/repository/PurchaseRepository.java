package br.com.alg.scg.domain.purchases.entity.repository;

import br.com.alg.scg.domain.purchases.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
}
