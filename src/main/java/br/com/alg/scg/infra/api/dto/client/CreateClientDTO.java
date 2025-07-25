package br.com.alg.scg.infra.api.dto.client;

import br.com.alg.scg.infra.api.dto.common.AddressDTO;
import br.com.alg.scg.infra.api.dto.common.ContactDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClientDTO(
        @JsonProperty("name")
        @NotBlank(message = "Nome do cliente é obrigatório")
        String name,
        
        @JsonProperty("contact")
        @NotNull(message = "Contato é obrigatório")
        @Valid
        ContactDTO contact,
        
        @JsonProperty("delivery_address")
        @NotNull(message = "Endereço de entrega é obrigatório")
        @Valid
        AddressDTO deliveryAddress
) {}