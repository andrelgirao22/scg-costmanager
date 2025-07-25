package br.com.alg.scg.infra.api.dto;

import br.com.alg.scg.domain.common.valueobject.Address;
import br.com.alg.scg.domain.common.valueobject.Contact;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.PurchaseItem;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.Sale;
import br.com.alg.scg.domain.sales.entity.SaleItem;
import br.com.alg.scg.infra.api.dto.client.ClientDTO;
import br.com.alg.scg.infra.api.dto.common.AddressDTO;
import br.com.alg.scg.infra.api.dto.common.ContactDTO;
import br.com.alg.scg.infra.api.dto.product.ProductDTO;
import br.com.alg.scg.infra.api.dto.purchase.PurchaseDTO;
import br.com.alg.scg.infra.api.dto.purchase.PurchaseItemDTO;
import br.com.alg.scg.infra.api.dto.sale.SaleDTO;
import br.com.alg.scg.infra.api.dto.sale.SaleItemDTO;
import br.com.alg.scg.infra.api.dto.supplier.SupplierDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class DTOMapper {
    
    // ==================== PRODUCT MAPPINGS ====================
    
    public ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getStock()
        );
    }
    
    // ==================== CLIENT MAPPINGS ====================
    
    public ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getName(),
                toDTO(client.getContact()),
                client.getDeliveryAddress() != null ? toDTO(client.getDeliveryAddress()) : null,
                client.getStatus(),
                client.getRegistrationDate()
        );
    }
    
    public Contact toEntity(ContactDTO dto) {
        return new Contact(dto.email(), dto.phone());
    }
    
    public ContactDTO toDTO(Contact contact) {
        return new ContactDTO(contact.email(), contact.phone());
    }
    
    public Address toEntity(AddressDTO dto) {
        if (dto == null) return null;
        return new Address(dto.street(), dto.city(), dto.postalCode());
    }
    
    public AddressDTO toDTO(Address address) {
        if (address == null) return null;
        return new AddressDTO(address.street(), address.city(), address.postalCode());
    }
    
    // ==================== SUPPLIER MAPPINGS ====================
    
    public SupplierDTO toDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getDocument(),
                toDTO(supplier.getContact())
        );
    }
    
    // ==================== SALE MAPPINGS ====================
    
    public SaleDTO toDTO(Sale sale) {
        return new SaleDTO(
                sale.getId(),
                toDTO(sale.getClient()),
                sale.getSaleDate(),
                sale.getTotalValue().value(),
                sale.getItems().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }
    
    public SaleItemDTO toDTO(SaleItem saleItem) {
        return new SaleItemDTO(
                saleItem.getId(),
                toDTO(saleItem.getProduct()),
                new BigDecimal(saleItem.getQuantity()),
                saleItem.getUnitPrice().value(),
                saleItem.getSubtotal().value()
        );
    }
    
    // ==================== PURCHASE MAPPINGS ====================
    
    public PurchaseDTO toDTO(Purchase purchase) {
        return new PurchaseDTO(
                purchase.getId(),
                toDTO(purchase.getSupplier()),
                purchase.getDate().toLocalDate(),
                purchase.getTotalCost().value(),
                purchase.getItems().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }
    
    public PurchaseItemDTO toDTO(PurchaseItem purchaseItem) {
        return new PurchaseItemDTO(
                purchaseItem.getId(),
                toDTO(purchaseItem.getProduct()),
                purchaseItem.getQuantity().value(),
                purchaseItem.getQuantity().unitMeasurement().toString(),
                purchaseItem.getUnitCost().value(),
                purchaseItem.getSubtotal().value()
        );
    }
}