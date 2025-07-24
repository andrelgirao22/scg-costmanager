package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePurchaseDTO(
        @JsonProperty("supplier_id")
        UUID supplierId,
        
        @JsonProperty("purchase_date")
        LocalDate purchaseDate
) {}