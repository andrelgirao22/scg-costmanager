package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateSaleDTO(
        @JsonProperty("client_id")
        UUID clientId
) {}