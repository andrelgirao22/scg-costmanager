package br.com.alg.scg.domain.finance.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record ProfitMargin(BigDecimal percent) {

    public ProfitMargin {
        if(percent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Margem de lucro não pode ser negativa.");
        }
    }

}
