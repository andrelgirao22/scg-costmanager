package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressDTO(
        @JsonProperty("street")
        String street,
        
        @JsonProperty("city")
        String city,
        
        @JsonProperty("postal_code")
        String postalCode
) {}