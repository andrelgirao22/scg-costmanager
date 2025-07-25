package br.com.alg.scg.infra.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactDTO(
        @JsonProperty("email")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ter formato válido")
        String email,
        
        @JsonProperty("phone")
        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\(\\d{2}\\)\\s?\\d{4,5}-?\\d{4}", message = "Telefone deve ter formato (11) 99999-9999")
        String phone
) {}