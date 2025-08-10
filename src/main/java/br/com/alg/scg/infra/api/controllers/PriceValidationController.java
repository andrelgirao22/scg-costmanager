package br.com.alg.scg.infra.api.controllers;

import br.com.alg.scg.application.service.PriceValidationService;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/price-validation")
@Tag(name = "Validação de Preços", description = "Correção de preços e unidades incorretas")
public class PriceValidationController {

    private final PriceValidationService priceValidationService;

    @Autowired
    public PriceValidationController(PriceValidationService priceValidationService) {
        this.priceValidationService = priceValidationService;
    }

    @GetMapping("/suspicious")
    @Operation(summary = "Listar produtos com preços suspeitos",
               description = "Identifica produtos que podem ter preços registrados com unidade incorreta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de produtos suspeitos retornada com sucesso")
    })
    public ResponseEntity<List<Product>> getSuspiciousProducts() {
        List<Product> suspicious = priceValidationService.findSuspiciousUnitProducts();
        return ResponseEntity.ok(suspicious);
    }

    @GetMapping("/report")
    @Operation(summary = "Relatório de análise de preços",
               description = "Gera relatório detalhado de todos os produtos com preços e unidades para análise manual")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<String> getPriceAnalysisReport() {
        String report = priceValidationService.generatePriceAnalysisReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/fix-price")
    @Operation(summary = "Corrigir preço e unidade de produto",
               description = "Corrige manualmente preço e unidades de um produto específico. " +
                           "Use para produtos que foram cadastrados com unidade incorreta nas compras.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Preço e unidade corrigidos com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<String> fixProductPrice(
            @Parameter(description = "ID do produto (UUID)", required = true)
            @RequestParam String productId,
            
            @Parameter(description = "Preço correto (ex: 0.60 para R$ 0,60)", required = true)
            @RequestParam BigDecimal correctPrice,
            
            @Parameter(description = "Unidade correta do preço", required = true)
            @RequestParam UnitMeasurement correctPriceUnit,
            
            @Parameter(description = "Unidade correta do estoque", required = true)
            @RequestParam UnitMeasurement correctStockUnit) {
        
        try {
            priceValidationService.fixProductPriceAndUnit(productId, correctPrice, correctPriceUnit, correctStockUnit);
            return ResponseEntity.ok("Preço e unidade corrigidos com sucesso para produto: " + productId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao corrigir preço: " + e.getMessage());
        }
    }
}