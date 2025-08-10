package br.com.alg.scg.domain.product.entity;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "prices")
public class Price {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Embedded
    private Money value;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_measurement", nullable = false)
    private UnitMeasurement unitMeasurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime effectiveDate;

    protected Price() {
        this.id = UuidCreator.getTimeOrderedEpoch();
    }

    public Price(Money value, LocalDateTime effectiveDate, Product product) {
        super();
        this.value = value;
        this.unitMeasurement = UnitMeasurement.KILOGRAM; // Default para compatibilidade
        this.effectiveDate = effectiveDate;
        this.product = product;
    }

    public Price(Money value, UnitMeasurement unitMeasurement, LocalDateTime effectiveDate, Product product) {
        super();
        this.value = value;
        this.unitMeasurement = unitMeasurement;
        this.effectiveDate = effectiveDate;
        this.product = product;
    }

}
