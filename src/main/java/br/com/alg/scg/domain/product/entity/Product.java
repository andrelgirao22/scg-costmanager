package br.com.alg.scg.domain.product.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.UnitConverter;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_unit", length = 20)
    private UnitMeasurement stockUnit;

    @Embedded
    @AttributeOverride(name = "percent", column = @Column(name = "profit_margin_percent"))
    private ProfitMargin profitMargin;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Price> pricesHistory = new ArrayList<>();

    protected Product() { /* Construtor exigido pelo JPA */ }

    // Factory method para criar produto a partir de formulário
    public static Product createFromForm(String name, ProductType type, BigDecimal stock) {
        Product product = new Product();
        product.name = Objects.requireNonNull(name, "Product name cannot be null");
        product.type = Objects.requireNonNull(type, "Product type cannot be null");
        product.stock = stock != null ? stock : BigDecimal.ZERO;
        return product;
    }

    // Método para inicialização antes da persistência
    @PrePersist
    private void prePersist() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.stock == null) {
            this.stock = BigDecimal.ZERO;
        }
        // Removido: não definir stockUnit automaticamente
        // A unidade deve ser definida na primeira compra
    }

    // Setters para binding de formulários
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Product name cannot be null");
    }

    public void setType(ProductType type) {
        this.type = Objects.requireNonNull(type, "Product type cannot be null");
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock != null ? stock : BigDecimal.ZERO;
    }

    public void setStockUnit(UnitMeasurement stockUnit) {
        if (this.type != ProductType.RAW_MATERIAL) {
            throw new IllegalStateException("Apenas matérias-primas têm unidade de estoque.");
        }
        this.stockUnit = stockUnit;
    }

    public static Product createRawMaterial(String name, BigDecimal initStock) {
        var product = new Product();
        product.id = UuidCreator.getTimeOrderedEpoch();
        product.name = name;
        product.type = ProductType.RAW_MATERIAL;
        product.stock = initStock;
        product.stockUnit = UnitMeasurement.KILOGRAM; // Padrão: kg
        return product;
    }

    public static Product createRawMaterial(String name, BigDecimal initStock, UnitMeasurement stockUnit) {
        var product = new Product();
        product.id = UuidCreator.getTimeOrderedEpoch();
        product.name = name;
        product.type = ProductType.RAW_MATERIAL;
        product.stock = initStock;
        product.stockUnit = stockUnit;
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

    public void addPrice(Money newPrice, UnitMeasurement unitMeasurement) {
        Objects.requireNonNull(newPrice, "O preço não pode ser nulo.");
        Objects.requireNonNull(unitMeasurement, "A unidade de medida não pode ser nula.");
        this.pricesHistory.add(new Price(newPrice, unitMeasurement, LocalDateTime.now(), this));
    }

    public Optional<Money> getCurrentPrice() {
        return pricesHistory.stream()
                .max((p1, p2) -> p1.getEffectiveDate().compareTo(p2.getEffectiveDate()))
                .map(Price::getValue);
    }

    public Optional<Price> getCurrentPriceDetails() {
        return pricesHistory.stream()
                .max((p1, p2) -> p1.getEffectiveDate().compareTo(p2.getEffectiveDate()));
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

    /**
     * Calcula o custo de uma quantidade específica desta matéria-prima
     * baseado no preço mais recente registrado, com conversão automática de unidades.
     * 
     * @param quantity Quantidade a calcular o custo
     * @return Custo total da quantidade
     */
    public Money calculateIngredientCost(Quantity quantity) {
        if (this.type != ProductType.RAW_MATERIAL) {
            throw new IllegalStateException("Cálculo de custo por quantidade só é aplicável a matérias-primas.");
        }
        
        Optional<Price> currentPriceDetails = getCurrentPriceDetails();
        if (currentPriceDetails.isEmpty()) {
            return Money.ZERO; // Sem preço definido
        }
        
        Price priceInfo = currentPriceDetails.get();
        Money unitPrice = priceInfo.getValue();
        UnitMeasurement priceUnit = priceInfo.getUnitMeasurement();
        
        // Converter a quantidade da receita para a mesma unidade do preço
        Quantity convertedQuantity;
        try {
            convertedQuantity = UnitConverter.convert(quantity, priceUnit);
        } catch (IllegalArgumentException e) {
            // Se não conseguir converter, usar a quantidade original (compatibilidade)
            convertedQuantity = quantity;
        }
        
        // Custo = preço unitário × quantidade convertida
        return unitPrice.multiply(convertedQuantity.value());
    }

    public UUID getId() { return id; }
    public String getName() { return name; }

    public ProductType getType() { return type; }

    public Optional<Recipe> getProductRecipe() { return Optional.ofNullable(recipe); }

    public ProfitMargin getProfitMargin() {
        return profitMargin;
    }

    public BigDecimal getStock() { return stock; }
    public UnitMeasurement getStockUnit() { return stockUnit; }
    public List<Price> getPricesHistory() { return List.copyOf(pricesHistory); }

}
