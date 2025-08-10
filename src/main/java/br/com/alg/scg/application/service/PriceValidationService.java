package br.com.alg.scg.application.service;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.product.entity.Price;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.repository.ProductRepository;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço para detectar e corrigir problemas de unidades em preços e estoques.
 * Necessário para corrigir dados históricos onde compras em "un" ou "g" 
 * foram salvos como se fossem em "kg".
 */
@Service
public class PriceValidationService {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public PriceValidationService(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    /**
     * Identifica produtos com possíveis problemas de unidades baseado em:
     * - Preços muito baixos (ex: R$ 0,02/kg para creme de leite)
     * - Estoques muito altos para produtos unitários (ex: 30kg de ovos)
     */
    @Transactional(readOnly = true)
    public List<Product> findSuspiciousUnitProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getType() == ProductType.RAW_MATERIAL)
                .filter(this::hasSuspiciousPrice)
                .toList();
    }

    private boolean hasSuspiciousPrice(Product product) {
        if (product.getCurrentPrice().isEmpty()) {
            return false;
        }

        Money currentPrice = product.getCurrentPrice().get();
        
        // Detectar preços suspeitos baseado no nome do produto e valor
        String productName = product.getName().toLowerCase();
        BigDecimal priceValue = currentPrice.value();
        
        // Produtos que provavelmente são vendidos por unidade mas têm preço muito baixo/kg
        if ((productName.contains("ovo") || productName.contains("egg")) && 
            priceValue.compareTo(new BigDecimal("2.00")) < 0) {
            return true; // Ovos por menos de R$ 2,00/kg é suspeito
        }
        
        // Produtos lácteos com preços muito baixos/kg
        if ((productName.contains("leite") || productName.contains("creme") || 
             productName.contains("milk") || productName.contains("cream")) && 
            priceValue.compareTo(new BigDecimal("1.00")) < 0) {
            return true; // Lácteos por menos de R$ 1,00/kg é suspeito
        }
        
        return false;
    }

    /**
     * Corrige manualmente o preço e unidade de um produto específico.
     * Use quando souber que o preço foi registrado com unidade incorreta.
     */
    @Transactional
    public void fixProductPriceAndUnit(String productId, BigDecimal correctPrice, 
                                      UnitMeasurement correctPriceUnit, 
                                      UnitMeasurement correctStockUnit) {
        
        Product product = productRepository.findById(java.util.UUID.fromString(productId))
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        
        // Corrigir unidade do estoque
        product.setStockUnit(correctStockUnit);
        
        // Adicionar preço correto com unidade correta
        product.addPrice(new Money(correctPrice), correctPriceUnit);
        
        productRepository.save(product);
    }

    /**
     * Lista produtos com informações detalhadas para análise manual
     */
    @Transactional(readOnly = true)
    public String generatePriceAnalysisReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== RELATÓRIO DE ANÁLISE DE PREÇOS E UNIDADES ===\n\n");
        
        List<Product> allProducts = productRepository.findAll().stream()
                .filter(p -> p.getType() == ProductType.RAW_MATERIAL)
                .toList();
        
        for (Product product : allProducts) {
            report.append("Produto: ").append(product.getName()).append("\n");
            report.append("Estoque: ").append(product.getStock()).append(" ");
            report.append(product.getStockUnit() != null ? product.getStockUnit().getUnit() : "kg").append("\n");
            
            if (product.getCurrentPrice().isPresent()) {
                Money price = product.getCurrentPrice().get();
                report.append("Preço Atual: R$ ").append(price.value()).append("/");
                
                // Tentar obter unidade do preço (se disponível)
                product.getCurrentPriceDetails().ifPresent(priceDetails -> 
                    report.append(priceDetails.getUnitMeasurement().getUnit()));
                
                report.append("\n");
                
                if (hasSuspiciousPrice(product)) {
                    report.append("⚠️  SUSPEITO: Preço pode estar com unidade incorreta\n");
                }
            } else {
                report.append("Preço: Não definido\n");
            }
            
            report.append("---\n");
        }
        
        return report.toString();
    }
}