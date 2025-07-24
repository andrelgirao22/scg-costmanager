package br.com.alg.scg.application.service;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.Sale;
import br.com.alg.scg.domain.sales.entity.SaleItem;
import br.com.alg.scg.domain.sales.entity.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final ClientService clientService;

    @Autowired
    public SaleService(SaleRepository saleRepository, ProductService productService, ClientService clientService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.clientService = clientService;
    }

    // ==================== CREATE OPERATIONS ====================

    @Transactional
    public Sale createSale(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        if (!clientService.isActive(client.getId())) {
            throw new IllegalArgumentException("Cliente não está ativo");
        }

        Sale sale = new Sale(client);
        return saleRepository.save(sale);
    }

    @Transactional
    public Sale createSaleWithProducts(Client client, List<Product> products, List<Integer> quantities) {
        if (client == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Lista de produtos não pode ser vazia");
        }
        if (quantities == null || quantities.size() != products.size()) {
            throw new IllegalArgumentException("Lista de quantidades deve ter o mesmo tamanho da lista de produtos");
        }
        if (!clientService.isActive(client.getId())) {
            throw new IllegalArgumentException("Cliente não está ativo");
        }

        // Verificar estoque antes de criar a venda
        validateStockAvailabilityForProducts(products, quantities);

        Sale sale = new Sale(client);
        
        for (int i = 0; i < products.size(); i++) {
            sale.addItem(products.get(i), quantities.get(i));
        }
        
        Sale savedSale = saleRepository.save(sale);
        
        // Reduzir estoque dos produtos vendidos
        updateProductStockForProducts(products, quantities);
        
        return savedSale;
    }

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public Optional<Sale> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return saleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return saleRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return saleRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Sale> findByClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        return saleRepository.findByClient(client);
    }

    @Transactional(readOnly = true)
    public List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Data inicial não pode ser nula");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Data final não pode ser nula");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }

    // ==================== UPDATE OPERATIONS ====================

    @Transactional
    public Sale addItem(UUID saleId, Product product, int quantity) {
        if (saleId == null) {
            throw new IllegalArgumentException("ID da venda não pode ser nulo");
        }
        if (product == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com ID: " + saleId));

        // Verificar se há estoque suficiente
        if (!productService.hasStock(product.getId(), new java.math.BigDecimal(quantity))) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + product.getName());
        }

        sale.addItem(product, quantity);
        
        Sale savedSale = saleRepository.save(sale);
        
        // Reduzir estoque do produto
        productService.decreaseStock(product.getId(), new java.math.BigDecimal(quantity));
        
        return savedSale;
    }

    @Transactional
    public Sale removeItem(UUID saleId, UUID itemId) {
        if (saleId == null) {
            throw new IllegalArgumentException("ID da venda não pode ser nulo");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("ID do item não pode ser nulo");
        }

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com ID: " + saleId));

        SaleItem itemToRemove = sale.findItemById(itemId);
        if (itemToRemove == null) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + itemId);
        }

        // Usar método da entidade para remover item
        sale.removeItem(itemToRemove);
        
        Sale savedSale = saleRepository.save(sale);
        
        // Restaurar estoque do produto
        productService.increaseStock(itemToRemove.getProduct().getId(), new java.math.BigDecimal(itemToRemove.getQuantity()));
        
        return savedSale;
    }

    // ==================== DELETE OPERATIONS ====================

    @Transactional
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com ID: " + id));
        
        // Restaurar estoque antes de deletar
        restoreProductStock(sale.getItems());
        
        saleRepository.deleteById(id);
    }

    @Transactional
    public void delete(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Venda não pode ser nula");
        }
        
        // Restaurar estoque antes de deletar
        restoreProductStock(sale.getItems());
        
        saleRepository.delete(sale);
    }

    // ==================== BUSINESS OPERATIONS ====================

    @Transactional(readOnly = true)
    public Money calculateTotalValue(UUID saleId) {
        if (saleId == null) {
            throw new IllegalArgumentException("ID da venda não pode ser nulo");
        }

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com ID: " + saleId));

        return sale.getTotalValue();
    }

    @Transactional(readOnly = true)
    public int getItemCount(UUID saleId) {
        if (saleId == null) {
            throw new IllegalArgumentException("ID da venda não pode ser nulo");
        }

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com ID: " + saleId));

        return sale.getItems().size();
    }

    @Transactional(readOnly = true)
    public Money calculateTotalSalesForPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Data inicial não pode ser nula");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Data final não pode ser nula");
        }

        List<Sale> sales = findByDateRange(startDate, endDate);
        
        BigDecimal total = sales.stream()
                .map(Sale::getTotalValue)
                .map(Money::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Money(total);
    }

    @Transactional(readOnly = true)
    public long countSalesForPeriod(LocalDate startDate, LocalDate endDate) {
        return findByDateRange(startDate, endDate).size();
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void validateStockAvailabilityForProducts(List<Product> products, List<Integer> quantities) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            if (!productService.hasStock(product.getId(), new java.math.BigDecimal(quantity))) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + product.getName());
            }
        }
    }

    private void updateProductStockForProducts(List<Product> products, List<Integer> quantities) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            productService.decreaseStock(product.getId(), new java.math.BigDecimal(quantity));
        }
    }

    private void restoreProductStock(List<SaleItem> items) {
        for (SaleItem item : items) {
            productService.increaseStock(item.getProduct().getId(), new java.math.BigDecimal(item.getQuantity()));
        }
    }
}