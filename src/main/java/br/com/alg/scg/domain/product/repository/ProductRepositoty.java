package br.com.alg.scg.domain.product.repository;

import br.com.alg.scg.domain.product.entity.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoty {

    Optional<Product> findById(UUID id);
    void save(Product product);
}
