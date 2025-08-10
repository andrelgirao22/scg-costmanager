package br.com.alg.scg.infra.web.views.product.dto;

/**
 * DTO para exibição da análise de custos em grid
 */
public class CostAnalysisDisplay {
    private String ingredientName;
    private String quantity;
    private String unitCost;
    private String totalCost;
    
    public CostAnalysisDisplay(String ingredientName, String quantity, String unitCost, String totalCost) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
    }
    
    public String getIngredientName() {
        return ingredientName;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public String getUnitCost() {
        return unitCost;
    }
    
    public String getTotalCost() {
        return totalCost;
    }
    
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public void setUnitCost(String unitCost) {
        this.unitCost = unitCost;
    }
    
    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}