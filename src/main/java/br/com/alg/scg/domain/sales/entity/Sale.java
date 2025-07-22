package br.com.alg.scg.domain.sales.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_value", nullable = false))
    private Money totalValue;

    public Sale(Client client) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.client = Objects.requireNonNull(client, "Client cannot be null for a sale.");
        this.saleDate = LocalDateTime.now();
        this.totalValue = Money.ZERO;
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if(product.getType() != ProductType.FINAL_PRODUCT) {
            throw new IllegalArgumentException("Somente produto final pode ser vendido");
        }

        // Regra de negócio: O preço do item é "congelado" no momento da venda.
        Money priceAtSaleTime  = product.getCurrentPrice()
                .orElseThrow(() -> new IllegalStateException("Produto " + product.getName() + " não possui preço para venda."));

        var novoItem = new SaleItem(this, product, quantity, priceAtSaleTime );
        this.items.add(novoItem);

        // Atualiza o valor total da venda
        this.totalValue = this.totalValue.sum(novoItem.getSubtotal());
    }

    // Getters
    public List<SaleItem> getItems() {
        return List.copyOf(items); // Retorna uma cópia para proteger a lista original
    }
}