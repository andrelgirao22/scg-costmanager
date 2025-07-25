package br.com.alg.scg.infra.api.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record UpdateProductNameDTO(
        @JsonProperty("name")
        @NotBlank(message = "Nome do produto é obrigatório")
        String name
) {}