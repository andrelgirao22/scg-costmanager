package br.com.alg.scg.infra.api.dto.supplier;

import br.com.alg.scg.infra.api.dto.common.ContactDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateSupplierDTO(
        @JsonProperty("name")
        @NotBlank(message = "Nome do fornecedor é obrigatório")
        String name,
        
        @JsonProperty("document")
        @NotBlank(message = "Documento é obrigatório")
        @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14}", message = "Documento deve ser CNPJ válido (00.000.000/0000-00)")
        String document,
        
        @JsonProperty("contact")
        @NotNull(message = "Contato é obrigatório")
        @Valid
        ContactDTO contact
) {}