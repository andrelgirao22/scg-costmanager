package br.com.alg.scg.domain.purchases.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class PurchaseItem {

    private final UUID rawMaterialId;
    private final Quantity quantity;
    private final Money unitCost;
    private final Money subtotal;

    public PurchaseItem(UUID rawMaterialId, Quantity quantity, Money unitCost) {
        Objects.requireNonNull(rawMaterialId, "Raw material ID cannot be null.");
        Objects.requireNonNull(quantity, "Quantity cannot be null.");
        Objects.requireNonNull(unitCost, "Unit cost cannot be null.");

        this.rawMaterialId = rawMaterialId;
        this.quantity = quantity;
        this.unitCost = unitCost;

        // A regra de negócio principal é calcular o subtotal na criação.
        this.subtotal = calculateSubtotal();
    }

    private Money calculateSubtotal() {
        // Multiplica o valor do Money pelo valor da Quantity.
        return this.unitCost.multiply(this.quantity.value());
    }
}
