package br.com.alg.scg.domain.sales.entity.repository;

import br.com.alg.scg.domain.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
}
