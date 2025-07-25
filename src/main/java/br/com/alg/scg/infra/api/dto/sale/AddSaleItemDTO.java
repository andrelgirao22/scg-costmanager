package br.com.alg.scg.infra.api.dto.sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddSaleItemDTO(
        @JsonProperty("product_id")
        @NotNull(message = "ID do produto é obrigatório")
        UUID productId,
        
        @JsonProperty("quantity")
        @NotNull(message = "Quantidade é obrigatória")
        @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
        BigDecimal quantity,
        
        @JsonProperty("unit_price")
        @NotNull(message = "Preço unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
        BigDecimal unitPrice
) {}