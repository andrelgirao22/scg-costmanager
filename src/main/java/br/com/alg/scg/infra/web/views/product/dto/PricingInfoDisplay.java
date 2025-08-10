package br.com.alg.scg.infra.web.views.product.dto;

/**
 * DTO para exibição das informações de precificação em grid
 */
public class PricingInfoDisplay {
    private String item;
    private String value;
    
    public PricingInfoDisplay(String item, String value) {
        this.item = item;
        this.value = value;
    }
    
    public String getItem() {
        return item;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setItem(String item) {
        this.item = item;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}