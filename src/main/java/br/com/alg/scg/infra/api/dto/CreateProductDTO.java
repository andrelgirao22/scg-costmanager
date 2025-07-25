package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductDTO(
        @JsonProperty("name")
        @NotBlank(message = "Nome do produto é obrigatório")
        String name,
        
        @JsonProperty("initial_stock")
        @NotNull(message = "Estoque inicial é obrigatório")
        @DecimalMin(value = "0.0", message = "Estoque inicial deve ser maior ou igual a zero")
        BigDecimal initialStock
) {}