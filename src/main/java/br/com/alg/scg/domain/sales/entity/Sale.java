package br.com.alg.scg.domain.sales.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sale {

    private Long id;
    private Long clientId;
    private LocalDateTime date;
    private List<SaleItem> items = new ArrayList<>();
    private Money totalValue;

    public Sale(Long clientId) {
        this.clientId = Objects.requireNonNull(clientId);
        this.date = LocalDateTime.now();
        this.totalValue = Money.ZERO;
    }

    public void adicionarItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        // Regra de negócio: O preço do item é "congelado" no momento da venda.
        Money precoNoMomentoDaVenda = product.getCurrentPrice()
                .orElseThrow(() -> new IllegalStateException("Produto " + product.getName() + " não possui preço para venda."));

        var novoItem = new SaleItem(product.getId(), quantity, precoNoMomentoDaVenda);
        this.items.add(novoItem);

        // Atualiza o valor total da venda
        this.totalValue = this.totalValue.sum(novoItem.getSubtotal());
    }

    // Getters
    public Long getId() { return id; }
    public Long getIdCliente() { return clientId; }
    public LocalDateTime getSaleDate() { return date; }
    public List<SaleItem> getItens() { return List.copyOf(items); }
    public Money getTotalValue() { return totalValue; }

}
