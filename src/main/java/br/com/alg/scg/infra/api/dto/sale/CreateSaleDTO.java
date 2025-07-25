package br.com.alg.scg.infra.api.dto.sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record CreateSaleDTO(
        @JsonProperty("client_id")
        @NotNull(message = "ID do cliente é obrigatório")
        UUID clientId,
        
        @JsonProperty("sale_date")
        @NotNull(message = "Data da venda é obrigatória")
        @PastOrPresent(message = "Data da venda deve ser hoje ou no passado")
        LocalDate saleDate
) {}