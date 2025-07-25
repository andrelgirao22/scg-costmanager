package br.com.alg.scg.infra.api.dto.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddPurchaseItemDTO(
        @JsonProperty("product_id")
        @NotNull(message = "ID do produto é obrigatório")
        UUID productId,
        
        @JsonProperty("quantity")
        @NotNull(message = "Quantidade é obrigatória")
        @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
        BigDecimal quantity,
        
        @JsonProperty("unit_measurement")
        @NotBlank(message = "Unidade de medida é obrigatória")
        String unitMeasurement,
        
        @JsonProperty("unit_cost")
        @NotNull(message = "Custo unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "Custo unitário deve ser maior que zero")
        BigDecimal unitCost
) {}