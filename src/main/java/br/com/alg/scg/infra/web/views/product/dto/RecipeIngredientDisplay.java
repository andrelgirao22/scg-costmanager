package br.com.alg.scg.infra.web.views.product.dto;

/**
 * DTO para exibição dos ingredientes da receita em grid
 */
public class RecipeIngredientDisplay {
    private String ingredientName;
    private String quantity;
    
    public RecipeIngredientDisplay(String ingredientName, String quantity) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }
    
    public String getIngredientName() {
        return ingredientName;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}