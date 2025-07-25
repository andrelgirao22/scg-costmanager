package br.com.alg.scg.infra.api.dto.supplier;

import br.com.alg.scg.infra.api.dto.common.ContactDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record SupplierDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("name")
        String name,
        
        @JsonProperty("document")
        String document,
        
        @JsonProperty("contact")
        ContactDTO contact
) {}