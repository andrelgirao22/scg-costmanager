package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AddSaleItemDTO(
        @JsonProperty("product_id")
        UUID productId,
        
        @JsonProperty("quantity")
        Integer quantity
) {}