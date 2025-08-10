package br.com.alg.scg.domain.product.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.RecipeIngredient;
import br.com.alg.scg.domain.product.repository.ProductRepository;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @OneToOne(mappedBy = "recipe", fetch = FetchType.LAZY)
    private  Product product;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    @Column(name = "yield_quantity", precision = 10, scale = 3)
    private BigDecimal yieldQuantity;

    public Recipe() {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.yieldQuantity = BigDecimal.ONE; // Default: 1 unidade
    }

    public Recipe(BigDecimal yieldQuantity) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.yieldQuantity = yieldQuantity != null ? yieldQuantity : BigDecimal.ONE;
    }

    public void addIngredient(Product rawMaterial, Quantity quantity) {
        if (rawMaterial.getType() != ProductType.RAW_MATERIAL) {
            throw new IllegalArgumentException("Apenas matérias-primas podem ser usadas como ingredientes.");
        }
        // Valida se o ingrediente já existe para evitar duplicatas
        if (ingredients.stream().anyMatch(i -> i.getRawMaterialId().equals(rawMaterial.getId()))) {
            throw new IllegalArgumentException("Ingrediente já existe na receita.");
        }
        this.ingredients.add(new RecipeIngredient(rawMaterial.getId(), quantity, this));
    }

    public void updateIngredient(UUID rawMaterialId, Quantity newQuantity) {
        RecipeIngredient existingIngredient = ingredients.stream()
                .filter(i -> i.getRawMaterialId().equals(rawMaterialId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente não encontrado na receita"));
        
        // Remove o ingrediente existente
        ingredients.remove(existingIngredient);
        
        // Adiciona com a nova quantidade
        this.ingredients.add(new RecipeIngredient(rawMaterialId, newQuantity, this));
    }

    public void removeIngredient(UUID rawMaterialId) {
        RecipeIngredient ingredientToRemove = ingredients.stream()
                .filter(i -> i.getRawMaterialId().equals(rawMaterialId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente não encontrado na receita"));
        
        this.ingredients.remove(ingredientToRemove);
    }

    public boolean hasIngredient(UUID rawMaterialId) {
        return ingredients.stream()
                .anyMatch(i -> i.getRawMaterialId().equals(rawMaterialId));
    }

    /**
     * Calcula o custo total da receita.
     * Esta é uma operação complexa que depende de dados externos (preços de outros produtos),
     * por isso, recebe o repositório como dependência para buscar as informações.
     * Isso é um padrão comum em DDD para manter a entidade rica em comportamento.
     */
    public Money calcTotalCost(ProductRepository productRepository) {
        if (ingredients.isEmpty()) {
            return Money.ZERO;
        }

        Money totalCost = Money.ZERO;

        for (RecipeIngredient recipeIngredient : ingredients) {
            Product rawMaterial = productRepository.findById(recipeIngredient.getRawMaterialId())
                    .orElseThrow(() -> new IllegalStateException("Matéria-prima não encontrada: " + recipeIngredient.getRawMaterialId()));

            if (rawMaterial.getCurrentPrice().isEmpty()) {
                throw new IllegalStateException("Matéria-prima sem preço definido: " + rawMaterial.getName() + 
                    ". É necessário registrar uma compra para definir o preço desta matéria-prima.");
            }

            // Usar o novo método que faz conversão automática de unidades
            Money ingredientCost = rawMaterial.calculateIngredientCost(recipeIngredient.getQuantity());
            totalCost = totalCost.sum(ingredientCost);
        }

        return totalCost;
    }

    /**
     * Calcula o custo unitário da receita (custo total ÷ rendimento).
     * Ex: Receita de brownie custa R$ 14,45 e rende 7 unidades = R$ 2,06 por unidade
     */
    public Money calcUnitCost(ProductRepository productRepository) {
        Money totalCost = calcTotalCost(productRepository);
        if (yieldQuantity == null || yieldQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Rendimento da receita deve ser maior que zero");
        }
        return new Money(totalCost.value().divide(yieldQuantity, 2, RoundingMode.HALF_UP));
    }

    public void setYieldQuantity(BigDecimal yieldQuantity) {
        if (yieldQuantity != null && yieldQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rendimento deve ser maior que zero");
        }
        this.yieldQuantity = yieldQuantity != null ? yieldQuantity : BigDecimal.ONE;
    }

    public BigDecimal getYieldQuantity() {
        return yieldQuantity != null ? yieldQuantity : BigDecimal.ONE;
    }

    public UUID getId() {
        return id;
    }

    public List<RecipeIngredient> getRecipes() {
        return List.copyOf(ingredients);
    }

    void setProduct(Product product) {
        this.product = product;
    }

}
