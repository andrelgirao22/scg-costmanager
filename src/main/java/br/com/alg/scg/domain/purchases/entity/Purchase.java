package br.com.alg.scg.domain.purchases.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@ToString(exclude = "items")
@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    private LocalDateTime date;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "total_cost_amount"))
    })
    private Money totalCost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    public Purchase() {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.date = LocalDateTime.now();
        this.totalCost = Money.ZERO;
    }

    public void addItem(Product product, Quantity quantity, Money unitCost) {
        if(quantity.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        var newItem = new PurchaseItem(this, product, quantity, unitCost);
        this.items.add(newItem);
        this.totalCost = this.totalCost.sum(newItem.getSubtotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return id != null && id.equals(purchase.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}