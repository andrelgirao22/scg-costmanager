package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SaleDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("client")
        ClientDTO client,
        
        @JsonProperty("sale_date")
        LocalDateTime saleDate,
        
        @JsonProperty("items")
        List<SaleItemDTO> items,
        
        @JsonProperty("total_value")
        BigDecimal totalValue
) {}