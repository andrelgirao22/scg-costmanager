package br.com.alg.scg.domain.common.valueobject;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String street, String city, String postalCode) {

    public Address {
        // Validações podem ser adicionadas aqui
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be blank.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be blank.");
        }
    }
}
