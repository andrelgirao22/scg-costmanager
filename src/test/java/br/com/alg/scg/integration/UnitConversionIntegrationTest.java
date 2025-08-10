package br.com.alg.scg.integration;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.repository.ProductRepository;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UnitConversionIntegrationTest {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductRepository productRepository;

    @Test
    void whenPurchaseInKgAndRecipeInGrams_shouldCalculateCorrectCost() {
        // Criar produto de matéria-prima
        Product chocolate = Product.createRawMaterial("Chocolate em Barra", BigDecimal.ZERO);
        chocolate = productRepository.save(chocolate);
        
        // Simular compra: preço R$ 26,55 por KG
        productService.addPrice(chocolate.getId(), new Money(new BigDecimal("26.55")), UnitMeasurement.KILOGRAM);
        
        // Criar receita que usa 200g de chocolate
        Quantity chocolateQuantity = new Quantity(new BigDecimal("200"), UnitMeasurement.GRAMA);
        
        // Calcular custo: 200g * R$ 26,55/kg = 0.2kg * R$ 26,55 = R$ 5,31
        Money calculatedCost = chocolate.calculateIngredientCost(chocolateQuantity);
        
        // Verificar se o custo está correto
        BigDecimal expectedCost = new BigDecimal("5.31");
        assertEquals(0, expectedCost.compareTo(calculatedCost.value()),
            "Custo deve ser R$ 5,31 para 200g quando preço é R$ 26,55/kg");
    }

    @Test
    void whenPurchaseAndRecipeInSameUnit_shouldCalculateCorrectly() {
        // Criar produto de matéria-prima
        Product farinha = Product.createRawMaterial("Farinha de Trigo", BigDecimal.ZERO);
        farinha = productRepository.save(farinha);
        
        // Simular compra: preço R$ 5,00 por KG
        productService.addPrice(farinha.getId(), new Money(new BigDecimal("5.00")), UnitMeasurement.KILOGRAM);
        
        // Criar receita que usa 0.5kg de farinha
        Quantity farinhaQuantity = new Quantity(new BigDecimal("0.5"), UnitMeasurement.KILOGRAM);
        
        // Calcular custo: 0.5kg * R$ 5,00/kg = R$ 2,50
        Money calculatedCost = farinha.calculateIngredientCost(farinhaQuantity);
        
        // Verificar se o custo está correto
        BigDecimal expectedCost = new BigDecimal("2.50");
        assertEquals(0, expectedCost.compareTo(calculatedCost.value()),
            "Custo deve ser R$ 2,50 para 0.5kg quando preço é R$ 5,00/kg");
    }

    @Test
    void whenRecipeWithMultipleIngredientsInDifferentUnits_shouldCalculateTotalCost() {
        // Criar produtos de matéria-prima
        Product chocolate = Product.createRawMaterial("Chocolate", BigDecimal.ZERO);
        Product farinha = Product.createRawMaterial("Farinha", BigDecimal.ZERO);
        
        chocolate = productRepository.save(chocolate);
        farinha = productRepository.save(farinha);
        
        // Definir preços em KG
        productService.addPrice(chocolate.getId(), new Money(new BigDecimal("26.55")), UnitMeasurement.KILOGRAM);
        productService.addPrice(farinha.getId(), new Money(new BigDecimal("5.00")), UnitMeasurement.KILOGRAM);
        
        // Criar produto final e receita
        Product brownie = Product.createFinalProduct("Brownie");
        brownie = productRepository.save(brownie);
        
        Recipe recipe = new Recipe(BigDecimal.ONE);
        recipe.addIngredient(chocolate, new Quantity(new BigDecimal("200"), UnitMeasurement.GRAMA)); // 200g chocolate
        recipe.addIngredient(farinha, new Quantity(new BigDecimal("150"), UnitMeasurement.GRAMA)); // 150g farinha
        
        brownie.defineRecipe(recipe);
        brownie = productRepository.save(brownie);
        
        // Calcular custo total da receita
        Money totalCost = recipe.calcTotalCost(productRepository);
        
        // Custo esperado: (200g * R$ 26,55/kg) + (150g * R$ 5,00/kg) = R$ 5,31 + R$ 0,75 = R$ 6,06
        BigDecimal expectedTotalCost = new BigDecimal("6.06");
        assertEquals(0, expectedTotalCost.compareTo(totalCost.value()),
            "Custo total deve ser R$ 6,06");
    }
}