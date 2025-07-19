package br.com.alg.scg.domain.product.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.RecipeIngredient;
import br.com.alg.scg.domain.product.repository.ProductRepositoty;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
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

    public Recipe() {
        this.id = UuidCreator.getTimeOrderedEpoch();
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

    /**
     * Calcula o custo total da receita.
     * Esta é uma operação complexa que depende de dados externos (preços de outros produtos),
     * por isso, recebe o repositório como dependência para buscar as informações.
     * Isso é um padrão comum em DDD para manter a entidade rica em comportamento.
     */
    public Money calcTotalCost(ProductRepositoty productRepositoty) {
        if (ingredients.isEmpty()) {
            return Money.ZERO;
        }

        Money totalCost = Money.ZERO;

        for (RecipeIngredient recipeIngredient : ingredients) {
            Product rawMaterial = productRepositoty.findById(recipeIngredient.getRawMaterialId())
                    .orElseThrow(() -> new IllegalStateException("Matéria-prima não encontrada: " + recipeIngredient.getRawMaterialId()));

            Money priceRawMaterial = rawMaterial.getCurrentPrice()
                    .orElseThrow(() -> new IllegalStateException("Matéria-prima sem preço definido: " + rawMaterial.getName()));

            //TODO: Aqui precisaria de uma lógica de conversão de unidades
            // Ex: Se o preço é por KG e a receita usa GRAMA
            BigDecimal quantityInRecipe = recipeIngredient.getQuantity().value();

            totalCost = totalCost.sum(priceRawMaterial.multiply(quantityInRecipe));
        }

        return totalCost;
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
