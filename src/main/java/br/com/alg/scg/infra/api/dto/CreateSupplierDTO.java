package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateSupplierDTO(
        @JsonProperty("name")
        String name,
        
        @JsonProperty("document")
        String document,
        
        @JsonProperty("contact")
        ContactDTO contact
) {}