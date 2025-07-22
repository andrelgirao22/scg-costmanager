package br.com.alg.scg.domain.purchases.entity.repository;

import br.com.alg.scg.domain.purchases.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
}
