package br.com.alg.scg.infra.api.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDTO(
        @JsonProperty("street")
        @NotBlank(message = "Rua é obrigatória")
        String street,
        
        @JsonProperty("city")
        @NotBlank(message = "Cidade é obrigatória")
        String city,
        
        @JsonProperty("postal_code")
        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter formato 12345-678 ou 12345678")
        String postalCode
) {}