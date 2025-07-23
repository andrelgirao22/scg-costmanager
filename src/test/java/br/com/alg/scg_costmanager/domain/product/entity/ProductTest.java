package br.com.alg.scg_costmanager.domain.product.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.finance.valueobject.ProfitMargin;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    @DisplayName("Deve criar matéria-prima com sucesso")
    void createRawMaterial_shouldCreateProductCorretly() {
        String name = "Farinha de Trigo ";
        BigDecimal initialStock = BigDecimal.TEN;

        //when
        Product rawMaterial = Product.createRawMaterial(name, initialStock);

        // Then
        assertNotNull(rawMaterial.getId());
        assertEquals(name, rawMaterial.getName());
        assertEquals(ProductType.RAW_MATERIAL, rawMaterial.getType());
        assertEquals(initialStock, rawMaterial.getStock());
        assertTrue(rawMaterial.getPricesHistory().isEmpty());
    }

    @Test
    @DisplayName("Deve criar produto final com sucesso")
    void createFinalProduct_shouldCreateProductCorrectly() {
        // Given
        String name = "Brownie de Chocolate";

        // When
        Product finalProduct = Product.createFinalProduct(name);

        // Then
        assertNotNull(finalProduct.getId());
        assertEquals(name, finalProduct.getName());
        assertEquals(ProductType.FINAL_PRODUCT, finalProduct.getType());
        assertEquals(BigDecimal.ZERO, finalProduct.getStock());
        assertFalse(finalProduct.getProductRecipe().isPresent());
    }

    @Test
    @DisplayName("Deve adicionar preço a um produto e obter o preço atual")
    void addPrice_shouldUpdateCurrentPrice() {
        // Given
        Product product = Product.createRawMaterial("Chocolate em Pó", BigDecimal.TEN);
        Money price1 = new Money(new BigDecimal("20.00"));
        Money price2 = new Money(new BigDecimal("22.50"));

        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusSeconds(1); // Garantido que é depois

        // When
        product.addPrice(price1, time1);
        product.addPrice(price2, time2); // Preço mais recente

        // Then
        assertEquals(2, product.getPricesHistory().size());
        Optional<Money> currentPrice = product.getCurrentPrice();
        assertTrue(currentPrice.isPresent());
        assertEquals(price2, currentPrice.get());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar definir receita para matéria-prima")
    void defineRecipe_onRawMaterial_shouldThrowException() {
        // Given
        Product rawMaterial = Product.createRawMaterial("Ovos", BigDecimal.TEN);
        Recipe recipe = new Recipe();

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            rawMaterial.defineRecipe(recipe);
        });
        assertEquals("Apenas produtos finais podem ter uma receita", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aumentar e diminuir o estoque de uma matéria-prima")
    void stockManagement_onRawMaterial_shouldWorkCorrectly() {
        // Given
        Product rawMaterial = Product.createRawMaterial("Manteiga", new BigDecimal("5.0"));

        // When
        rawMaterial.increaseStock(new BigDecimal("2.5"));

        // Then
        assertEquals(new BigDecimal("7.5"), rawMaterial.getStock());

        // When
        rawMaterial.decreaseStock(new BigDecimal("1.5"));

        // Then
        assertEquals(new BigDecimal("6.0"), rawMaterial.getStock());
    }

    @Test
    @DisplayName("Deve lançar exceção ao diminuir estoque para valor insuficiente")
    void decreaseStock_withInsufficientStock_shouldThrowException() {
        // Given
        Product rawMaterial = Product.createRawMaterial("Açúcar", new BigDecimal("1.0"));

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            rawMaterial.decreaseStock(new BigDecimal("2.0"));
        });
        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("Deve definir a margem de lucro para um produto final")
    void defineProfitMargin_onFinalProduct_shouldSetMargin() {
        // Given
        Product finalProduct = Product.createFinalProduct("Torta de Limão");
        ProfitMargin margin = new ProfitMargin(new BigDecimal("0.40")); // 40%

        // When
        finalProduct.defineProfitMargin(margin);

        // Then
        assertEquals(margin, finalProduct.getProfitMargin());
    }

    @Test
    @DisplayName("Deve lançar exceção ao definir margem de lucro para matéria-prima")
    void defineProfitMargin_onRawMaterial_shouldThrowException() {
        // Given
        Product rawMaterial = Product.createRawMaterial("Leite Condensado", BigDecimal.TEN);
        ProfitMargin margin = new ProfitMargin(new BigDecimal("0.40"));

        // When & Then
        var exception = assertThrows(IllegalStateException.class, () -> {
            rawMaterial.defineProfitMargin(margin);
        });
        assertEquals("A margem de lucro só pode ser definida para produtos finais.", exception.getMessage());
    }

}
