package br.com.alg.scg.infra.api.dto.purchase;

import br.com.alg.scg.infra.api.dto.supplier.SupplierDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PurchaseDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("supplier")
        SupplierDTO supplier,
        
        @JsonProperty("purchase_date")
        LocalDate purchaseDate,
        
        @JsonProperty("total_amount")
        BigDecimal totalAmount,
        
        @JsonProperty("items")
        List<PurchaseItemDTO> items
) {}