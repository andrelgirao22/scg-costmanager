package br.com.alg.scg.domain.purchases.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.sales.entity.SaleItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Purchase {

    private Long id;
    private Long providerId;
    private LocalDateTime dataCompra;
    private List<PurchaseItem> items = new ArrayList<>();
    private Money custoTotal;

    public void adicionarItem(Product materiaPrima, Quantity quantity, Money custoUnitario) {
        // Validações...
        var newItem = new PurchaseItem(materiaPrima.getId(), quantity, custoUnitario);
        this.items.add(newItem);
        // Atualizar custoTotal...

        //this.custoTotal = this.custoTotal.sum(newItem.getSubTotal());
    }


}
