package br.com.alg.scg.domain.sales.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.product.entity.Product;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "sale_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleItem {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "unit_price", nullable = false))
    })
    private Money unitPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "subtotal", nullable = false))
    })
    private Money subtotal;

    protected SaleItem(Sale sale, Product product, int quantity, Money unitPrice) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.sale = sale;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
    }
}
