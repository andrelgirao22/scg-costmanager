package br.com.alg.scg.infra.api.dto.product;

import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.infra.api.validation.ValidProductCreation;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@ValidProductCreation
public record CreateProductDTO(
        @JsonProperty("name")
        @NotBlank(message = "Nome do produto é obrigatório")
        String name,
        
        @JsonProperty("type")
        @NotNull(message = "Tipo do produto é obrigatório")
        ProductType type,
        
        @JsonProperty("initial_stock")
        @DecimalMin(value = "0.0", message = "Estoque inicial deve ser maior ou igual a zero")
        BigDecimal initialStock
) {}