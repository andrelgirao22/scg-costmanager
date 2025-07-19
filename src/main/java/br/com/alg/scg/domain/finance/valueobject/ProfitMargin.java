package br.com.alg.scg.domain.finance.valueobject;

import java.math.BigDecimal;

public record ProfitMargin(BigDecimal percent) {

    public ProfitMargin {
        if(percent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Margem de lucro não pode ser negativa.");
        }
    }

}
