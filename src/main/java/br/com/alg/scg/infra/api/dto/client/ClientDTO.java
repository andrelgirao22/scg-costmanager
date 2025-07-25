package br.com.alg.scg.infra.api.dto.client;

import br.com.alg.scg.domain.sales.entity.ClientStatus;
import br.com.alg.scg.infra.api.dto.common.AddressDTO;
import br.com.alg.scg.infra.api.dto.common.ContactDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("name")
        @NotBlank
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