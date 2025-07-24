package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContactDTO(
        @JsonProperty("email")
        String email,
        
        @JsonProperty("phone")
        String phone
) {}