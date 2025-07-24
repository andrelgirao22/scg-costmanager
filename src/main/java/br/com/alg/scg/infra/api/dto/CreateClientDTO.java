package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateClientDTO(
        @JsonProperty("name")
        String name,
        
        @JsonProperty("contact")
        ContactDTO contact,
        
        @JsonProperty("delivery_address")
        AddressDTO deliveryAddress
) {}