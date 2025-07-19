package br.com.alg.scg.domain.common.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public record Quantity(
        BigDecimal value,

        @Enumerated(EnumType.STRING)
        UnitMeasurement unitMeasurement) {

    public Quantity {
        Objects.requireNonNull(value, "O valor não pode ser nulo.");

        Objects.requireNonNull(unitMeasurement, "A unidade de medida não pode ser nula.");

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva.");
        }
    }
}
