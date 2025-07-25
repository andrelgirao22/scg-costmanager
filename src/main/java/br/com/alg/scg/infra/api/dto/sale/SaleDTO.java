package br.com.alg.scg.infra.api.dto.sale;

import br.com.alg.scg.infra.api.dto.client.ClientDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SaleDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("client")
        ClientDTO client,
        
        @JsonProperty("sale_date")
        LocalDateTime saleDate,
        
        @JsonProperty("total_amount")
        BigDecimal totalAmount,
        
        @JsonProperty("items")
        List<SaleItemDTO> items
) {}