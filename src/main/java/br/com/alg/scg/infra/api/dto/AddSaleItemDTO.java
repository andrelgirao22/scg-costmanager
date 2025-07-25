package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddSaleItemDTO(
        @JsonProperty("product_id")
        @NotNull(message = "ID do produto é obrigatório")
        UUID productId,
        
        @JsonProperty("quantity")
        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantity
) {}