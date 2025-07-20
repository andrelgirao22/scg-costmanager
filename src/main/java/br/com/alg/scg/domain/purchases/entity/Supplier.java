package br.com.alg.scg.domain.purchases.entity;

import br.com.alg.scg.domain.common.valueobject.Contact;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String document;

    @Embedded
    private Contact contact; // Reutilizando o VO para email, telefone, etc.

    // Construtor protegido para JPA
    protected Supplier() {}

    // Construtor público para criar novos fornecedores
    public Supplier(String name, String document, Contact contact) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.name = Objects.requireNonNull(name, "Fornecedor não deve ser nulo.");
        this.document = document;
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return id != null && id.equals(supplier.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }



}
