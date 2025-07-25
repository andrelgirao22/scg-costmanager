package br.com.alg.scg.infra.api.dto.sale;

import br.com.alg.scg.infra.api.dto.product.ProductDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("product")
        ProductDTO product,
        
        @JsonProperty("quantity")
        BigDecimal quantity,
        
        @JsonProperty("unit_price")
        BigDecimal unitPrice,
        
        @JsonProperty("total_price")
        BigDecimal totalPrice
) {}