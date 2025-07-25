package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSaleDTO(
        @JsonProperty("client_id")
        @NotNull(message = "ID do cliente é obrigatório")
        UUID clientId
) {}