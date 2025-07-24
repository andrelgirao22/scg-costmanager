package br.com.alg.scg.infra.api.dto;

import br.com.alg.scg.domain.sales.entity.ClientStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("name")
        String name,
        
        @JsonProperty("contact")
        ContactDTO contact,
        
        @JsonProperty("delivery_address")
        AddressDTO deliveryAddress,
        
        @JsonProperty("status")
        ClientStatus status,
        
        @JsonProperty("registration_date")
        LocalDateTime registrationDate
) {}