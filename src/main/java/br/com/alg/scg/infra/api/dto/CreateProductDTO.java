package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CreateProductDTO(
        @JsonProperty("name")
        String name,
        
        @JsonProperty("initial_stock")
        BigDecimal initialStock
) {}