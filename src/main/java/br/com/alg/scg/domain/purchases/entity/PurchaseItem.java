package br.com.alg.scg.domain.purchases.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.product.entity.Product;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "purchase_items")
public class PurchaseItem {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "quantity_value")),
            @AttributeOverride(name = "unitMeasurement", column = @Column(name = "quantity_unit"))
    })
    private Quantity quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "unit_cost_amount"))
    })
    private Money unitCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "subtotal_amount"))
    })
    private Money subtotal;

    protected PurchaseItem() {}

    public PurchaseItem(Purchase purchase, Product product, Quantity quantity, Money unitCost) {
        Objects.requireNonNull(purchase, "Raw material ID cannot be null.");
        Objects.requireNonNull(quantity, "Quantity cannot be null.");
        Objects.requireNonNull(unitCost, "Unit cost cannot be null.");

        this.id = UuidCreator.getTimeOrderedEpoch();
        this.purchase = purchase;
        this.product = product;
        this.quantity = quantity;
        this.unitCost = unitCost;

        // A regra de negócio principal é calcular o subtotal na criação.
        this.subtotal = this.unitCost.multiply(this.quantity.value());
    }
}