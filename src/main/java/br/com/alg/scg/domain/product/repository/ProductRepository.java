package br.com.alg.scg.domain.product.repository;

import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.recipe WHERE p.type = :type")
    List<Product> findAllWithRecipesByType(@Param("type") ProductType type);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.pricesHistory WHERE p.id = :id")
    Optional<Product> findByIdWithPrices(@Param("id") UUID id);
}
