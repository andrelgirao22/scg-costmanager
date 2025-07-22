package br.com.alg.scg.domain.finance.service;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.finance.valueobject.ProfitMargin;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.repository.ProductRepositoty;
import br.com.alg.scg.domain.product.valueobject.ProductType;

import java.math.BigDecimal;

public class SalePriceCalculatorService {

    private final ProductRepositoty productRepositoty;

    public SalePriceCalculatorService(ProductRepositoty productRepositoty) {
        this.productRepositoty = productRepositoty;
    }

    public Money salePriceCalcule(Product product) {
        if(product.getType() != ProductType.FINAL_PRODUCT) {
            throw new IllegalArgumentException("Cálculo de preço de venda é aplicável apneas a produtos finais.");
        }

        Recipe recipe = product.getProductRecipe()
                .orElseThrow(() -> new IllegalArgumentException("Produto final deve ter uma receita definida para calcular o preço"));

        ProfitMargin profitMargin = product.getProfitMargin();
        if (profitMargin == null) {
            throw new IllegalStateException("Produto final deve ter uma margem de lucro definida.");
        }

        Money productionCost = recipe.calcTotalCost(productRepositoty);

        BigDecimal marginFactor = BigDecimal.ONE.add(profitMargin.percent());

        return productionCost.multiply(marginFactor);
    }
}
