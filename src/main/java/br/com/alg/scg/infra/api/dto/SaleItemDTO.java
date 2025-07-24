package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("product")
        ProductDTO product,
        
        @JsonProperty("quantity")
        Integer quantity,
        
        @JsonProperty("unit_price")
        BigDecimal unitPrice,
        
        @JsonProperty("subtotal")
        BigDecimal subtotal
) {}