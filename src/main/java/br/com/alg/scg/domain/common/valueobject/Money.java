package br.com.alg.scg.domain.common.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
public record Money(
        @Column(name = "amount", precision = 19, scale = 2)
        BigDecimal value) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(value, "O valor não pode ser nulo");
        if(value.scale() > 2) {
            //sempre 2 casas decimais
            value = value.setScale(2, RoundingMode.HALF_UP);
        }

        if(value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor não pode ser negativo");
        }
    }

    public Money sum(Money newValue) {
        return new Money(this.value.add(newValue.value()));
    }

    public Money minus(Money newValue) {
        return new Money(this.value.subtract(newValue.value));
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.value.multiply(factor));
    }

    @Override
    public String toString() {
        return String.format("R$ %.2f", this.value);
    }
}
