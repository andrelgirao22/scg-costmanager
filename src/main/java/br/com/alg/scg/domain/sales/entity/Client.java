package br.com.alg.scg.domain.sales.entity;

import br.com.alg.scg.domain.common.valueobject.Address;
import br.com.alg.scg.domain.common.valueobject.Contact;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Client {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false, length = 159)
    private String name;

    @Embedded
    private Contact contact;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "delivery_postal_code"))
    })
    private Address deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClientStatus status;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    public Client(String name, Contact contact, Address deliveryAddress) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.name = Objects.requireNonNull(name, "Client name cannot be null");
        this.contact = Objects.requireNonNull(contact, "Contact information cannot be null");
        this.deliveryAddress = deliveryAddress; // Endereço pode ser opcional
        this.status = ClientStatus.ACTIVE;
        this.registrationDate = LocalDateTime.now();
    }

    public void updateContact(Contact newContact) {
        Objects.requireNonNull(newContact, "New contact information cannot be null");
        this.contact = newContact;
    }

    public void changeDeliveryAddress(Address newAddress) {
        this.deliveryAddress = newAddress;
    }

    public void deactivate() {
        if (this.status == ClientStatus.INACTIVE) {
            throw new IllegalStateException("Client is already inactive.");
        }
        if (this.status == ClientStatus.BLOCKED) {
            throw new IllegalStateException("A blocked client cannot be deactivated directly.");
        }
        this.status = ClientStatus.INACTIVE;
    }

    public void block(String reason) {
        Objects.requireNonNull(reason, "A reason is required to block a client.");
        if (this.status == ClientStatus.BLOCKED) {
            return; // Já está bloqueado
        }
        this.status = ClientStatus.BLOCKED;
        // O ideal seria registrar o 'motivo' em algum log ou campo de auditoria
    }

    public void unblock() {
        if (this.status != ClientStatus.BLOCKED) {
            throw new IllegalStateException("Client is not blocked.");
        }
        this.status = ClientStatus.ACTIVE;
    }

}
