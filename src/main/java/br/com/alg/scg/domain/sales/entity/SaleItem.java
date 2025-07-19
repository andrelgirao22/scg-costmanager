package br.com.alg.scg.domain.sales.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class SaleItem {

    private UUID productId;
    private int quantidade;
    private Money precoUnitario; // Preço no momento da venda
    private Money subtotal;

    public SaleItem(UUID idProduto, int quantity, Money unitPrice) {
        this.productId = idProduto;
        this.quantidade = quantity;
        this.precoUnitario = unitPrice;
        this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
    }
}
