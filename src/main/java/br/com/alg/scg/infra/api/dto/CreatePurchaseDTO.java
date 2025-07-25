package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePurchaseDTO(
        @JsonProperty("supplier_id")
        @NotNull(message = "ID do fornecedor é obrigatório")
        UUID supplierId,
        
        @JsonProperty("purchase_date")
        @NotNull(message = "Data da compra é obrigatória")
        @PastOrPresent(message = "Data da compra não pode ser futura")
        LocalDate purchaseDate
) {}