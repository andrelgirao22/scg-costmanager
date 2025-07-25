package br.com.alg.scg.domain.product.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.finance.valueobject.ProfitMargin;
import br.com.alg.scg.domain.product.valueobject.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProductType type;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    private Recipe recipe;

    @Column(precision = 10, scale = 3)
    private BigDecimal stock;

    @Embedded
    @AttributeOverride(name = "percent", column = @Column(name = "profit_margin_percent"))
    private ProfitMargin profitMargin;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Price> pricesHistory = new ArrayList<>();

    protected Product() { /* Construtor exigido pelo JPA */ }

    public static Product createRawMaterial(String name, BigDecimal initStock) {
        var product = new Product();
        product.id = UuidCreator.getTimeOrderedEpoch();
        product.name = name;
        product.type = ProductType.RAW_MATERIAL;
        product.stock = initStock;
        return product;
    }

    public static Product createFinalProduct(String name) {
        var product = new Product();
        product.id = UuidCreator.getTimeOrderedEpoch();
        product.name = name;
        product.type = ProductType.FINAL_PRODUCT;
        product.stock = BigDecimal.ZERO;
        return product;
    }

    public void defineRecipe(Recipe newRecipe) {
        if (this.type != ProductType.FINAL_PRODUCT) {
            throw new IllegalArgumentException("Apenas produtos finais podem ter uma receita");
        }
        this.recipe = newRecipe;
        if(newRecipe != null) {
            newRecipe.setProduct(this);
        }
    }

    public void addPrice(Money newPrice) {
        Objects.requireNonNull(newPrice, "O preço não pode ser nulo.");
        this.pricesHistory.add(new Price(newPrice, LocalDateTime.now(), this));
    }

    public void addPrice(Money newPrice, LocalDateTime effectiveDate) {
        Objects.requireNonNull(newPrice, "O preço não pode ser nulo.");
        Objects.requireNonNull(effectiveDate, "A data efetiva não pode ser nula.");
        this.pricesHistory.add(new Price(newPrice, effectiveDate, this));
    }

    public Optional<Money> getCurrentPrice() {
        return pricesHistory.stream()
                .max((p1, p2) -> p1.getEffectiveDate().compareTo(p2.getEffectiveDate()))
                .map(Price::getValue);
    }

    public void decreaseStock(BigDecimal quantity) {
        if (this.type != ProductType.RAW_MATERIAL) {
            throw new IllegalStateException("Apenas matérias-primas controlam estoque.");
        }
        if (this.stock.compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Estoque insuficiente para " + this.name);
        }
        this.stock = this.stock.subtract(quantity);
    }

    public void increaseStock(BigDecimal quantity) {
        if (this.type != ProductType.RAW_MATERIAL) {
            throw new IllegalStateException("Apenas matérias-primas controlam estoque.");
        }

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade a ser adicionada deve ser positiva.");
        }

        this.stock = this.stock.add(quantity);
    }

    public void defineProfitMargin(ProfitMargin profitMargin) {
        if (this.type != ProductType.FINAL_PRODUCT && profitMargin != null) {
            throw new IllegalStateException("A margem de lucro só pode ser definida para produtos finais.");
        }
        this.profitMargin = profitMargin;
    }
    
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        this.name = newName.trim();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }

    public ProductType getType() { return type; }

    public Optional<Recipe> getProductRecipe() { return Optional.ofNullable(recipe); }

    public ProfitMargin getProfitMargin() {
        return profitMargin;
    }

    public BigDecimal getStock() { return stock; }
    public List<Price> getPricesHistory() { return List.copyOf(pricesHistory); }

}
