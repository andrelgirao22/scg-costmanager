package br.com.alg.scg.infra.api.dto;

import br.com.alg.scg.domain.product.valueobject.ProductType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("name")
        String name,
        
        @JsonProperty("type")
        ProductType type,
        
        @JsonProperty("stock")
        BigDecimal stock
) {}