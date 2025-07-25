package br.com.alg.scg.infra.api.dto.purchase;

import br.com.alg.scg.infra.api.dto.product.ProductDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseItemDTO(
        @JsonProperty("id")
        UUID id,
        
        @JsonProperty("product")
        ProductDTO product,
        
        @JsonProperty("quantity")
        BigDecimal quantity,
        
        @JsonProperty("unit_measurement")
        String unitMeasurement,
        
        @JsonProperty("unit_cost")
        BigDecimal unitCost,
        
        @JsonProperty("subtotal")
        BigDecimal subtotal
) {}