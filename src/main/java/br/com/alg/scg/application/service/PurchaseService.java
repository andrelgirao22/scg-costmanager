package br.com.alg.scg.application.service;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.PurchaseItem;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import br.com.alg.scg.domain.purchases.entity.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, ProductService productService) {
        this.purchaseRepository = purchaseRepository;
        this.productService = productService;
    }

    // ==================== CREATE OPERATIONS ====================

    @Transactional
    public Purchase createPurchase(Supplier supplier, LocalDate purchaseDate) {
        if (supplier == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Data da compra não pode ser nula");
        }

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setDate(purchaseDate.atStartOfDay());
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public Purchase createPurchaseWithItems(Supplier supplier, LocalDate purchaseDate, List<PurchaseItem> items) {
        if (supplier == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Data da compra não pode ser nula");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Lista de itens não pode ser vazia");
        }

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setDate(purchaseDate.atStartOfDay());
        
        for (PurchaseItem item : items) {
            purchase.addItem(item.getProduct(), item.getQuantity(), item.getUnitCost());
        }
        
        Purchase savedPurchase = purchaseRepository.save(purchase);
        
        // Atualizar estoque dos produtos comprados
        updateProductStock(items);
        
        return savedPurchase;
    }

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public Optional<Purchase> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return purchaseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Purchase> findAll() {
        return purchaseRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return purchaseRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return purchaseRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Purchase> findBySupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        return purchaseRepository.findBySupplier(supplier);
    }

    @Transactional(readOnly = true)
    public List<Purchase> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Data inicial não pode ser nula");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Data final não pode ser nula");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
        // Converter LocalDate para LocalDateTime para busca
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return purchaseRepository.findByDateBetween(startDateTime, endDateTime);
    }

    // ==================== UPDATE OPERATIONS ====================

    @Transactional
    public Purchase updatePurchase(UUID purchaseId, Supplier supplier, LocalDate purchaseDate) {
        if (purchaseId == null) {
            throw new IllegalArgumentException("ID da compra não pode ser nulo");
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Data da compra não pode ser nula");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada com ID: " + purchaseId));

        purchase.setSupplier(supplier);
        purchase.setDate(purchaseDate.atStartOfDay());
        
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public Purchase addItem(UUID purchaseId, Product product, Quantity quantity, Money unitPrice) {
        if (purchaseId == null) {
            throw new IllegalArgumentException("ID da compra não pode ser nulo");
        }
        if (product == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantidade não pode ser nula");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Preço unitário não pode ser nulo");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada com ID: " + purchaseId));

        purchase.addItem(product, quantity, unitPrice);
        
        Purchase savedPurchase = purchaseRepository.save(purchase);
        
        // Atualizar estoque do produto
        productService.increaseStock(product.getId(), quantity.value());
        
        return savedPurchase;
    }

    @Transactional
    public Purchase removeItem(UUID purchaseId, UUID itemId) {
        if (purchaseId == null) {
            throw new IllegalArgumentException("ID da compra não pode ser nulo");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("ID do item não pode ser nulo");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada com ID: " + purchaseId));

        PurchaseItem itemToRemove = purchase.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado com ID: " + itemId));

        // Remove item using entity method
        purchase.removeItem(itemToRemove);
        
        Purchase savedPurchase = purchaseRepository.save(purchase);
        
        // Reverter estoque do produto
        productService.decreaseStock(itemToRemove.getProduct().getId(), itemToRemove.getQuantity().value());
        
        return savedPurchase;
    }

    // ==================== DELETE OPERATIONS ====================

    @Transactional
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada com ID: " + id));
        
        // Reverter estoque antes de deletar
        revertProductStock(purchase.getItems());
        
        purchaseRepository.deleteById(id);
    }

    @Transactional
    public void delete(Purchase purchase) {
        if (purchase == null) {
            throw new IllegalArgumentException("Compra não pode ser nula");
        }
        
        // Reverter estoque antes de deletar
        revertProductStock(purchase.getItems());
        
        purchaseRepository.delete(purchase);
    }

    // ==================== BUSINESS OPERATIONS ====================

    @Transactional(readOnly = true)
    public Money calculateTotalValue(UUID purchaseId) {
        if (purchaseId == null) {
            throw new IllegalArgumentException("ID da compra não pode ser nulo");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada com ID: " + purchaseId));

        return purchase.getTotalCost();
    }

    @Transactional(readOnly = true)
    public int getItemCount(UUID purchaseId) {
        if (purchaseId == null) {
            throw new IllegalArgumentException("ID da compra não pode ser nulo");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada com ID: " + purchaseId));

        return purchase.getItems().size();
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void updateProductStock(List<PurchaseItem> items) {
        for (PurchaseItem item : items) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity().value());
        }
    }

    private void revertProductStock(List<PurchaseItem> items) {
        for (PurchaseItem item : items) {
            productService.decreaseStock(item.getProduct().getId(), item.getQuantity().value());
        }
    }
}