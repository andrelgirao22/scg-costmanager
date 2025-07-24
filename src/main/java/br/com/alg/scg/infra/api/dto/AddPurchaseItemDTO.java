package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record AddPurchaseItemDTO(
        @JsonProperty("product_id")
        UUID productId,
        
        @JsonProperty("quantity")
        BigDecimal quantity,
        
        @JsonProperty("unit_measurement")
        String unitMeasurement,
        
        @JsonProperty("unit_cost")
        BigDecimal unitCost
) {}