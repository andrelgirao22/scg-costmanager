package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("supplier")
        SupplierDTO supplier,
        
        @JsonProperty("date")
        LocalDateTime date,
        
        @JsonProperty("items")
        List<PurchaseItemDTO> items,
        
        @JsonProperty("total_cost")
        BigDecimal totalCost
) {}