package br.com.alg.scg.domain.common.valueobject;

import br.com.alg.scg.domain.product.entity.Recipe;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(name = "raw_material_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID rawMaterialId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "quantity_value",
            precision = 10, scale = 3)),
            @AttributeOverride(name = "unitMeasurement", column = @Column(name = "quantity_unit"))
    })
    private Quantity quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    protected RecipeIngredient() {}

    public RecipeIngredient(UUID rawMaterialId, Quantity quantity, Recipe recipe) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.rawMaterialId = rawMaterialId;
        this.quantity = quantity;
        this.recipe = recipe;
    }

    public UUID getRawMaterialId() { return rawMaterialId; }
    public Quantity getQuantity() { return quantity; }
}
