package br.com.alg.scg.application.service;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.finance.valueobject.ProfitMargin;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.repository.ProductRepository;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ==================== CREATE OPERATIONS ====================

    @Transactional
    public Product createProduct(String name, ProductType type, BigDecimal initialStock) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tipo do produto não pode ser nulo");
        }

        Product product;
        switch (type) {
            case RAW_MATERIAL:
                if (initialStock == null || initialStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Estoque inicial não pode ser negativo para matérias-primas");
                }
                product = Product.createRawMaterial(name, initialStock);
                break;
            case FINAL_PRODUCT:
                product = Product.createFinalProduct(name);
                break;
            default:
                throw new IllegalArgumentException("Tipo de produto não suportado: " + type);
        }
        
        return productRepository.save(product);
    }

    @Transactional
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        
        // Validações básicas
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        if (product.getType() == null) {
            throw new IllegalArgumentException("Tipo do produto não pode ser nulo");
        }
        
        return productRepository.save(product);
    }

    @Transactional
    public Product createRawMaterial(String name, BigDecimal initialStock) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        if (initialStock == null || initialStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estoque inicial não pode ser negativo");
        }

        Product rawMaterial = Product.createRawMaterial(name, initialStock);
        return productRepository.save(rawMaterial);
    }

    @Transactional
    public Product createFinalProduct(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }

        Product finalProduct = Product.createFinalProduct(name);
        return productRepository.save(finalProduct);
    }

    @Transactional
    public Product createFinalProductWithRecipe(String name, Recipe recipe, ProfitMargin profitMargin) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        if (recipe == null) {
            throw new IllegalArgumentException("Receita não pode ser nula");
        }
        if (profitMargin == null) {
            throw new IllegalArgumentException("Margem de lucro não pode ser nula");
        }

        Product finalProduct = Product.createFinalProduct(name);
        finalProduct.defineRecipe(recipe);
        finalProduct.defineProfitMargin(profitMargin);
        
        return productRepository.save(finalProduct);
    }

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public Optional<Product> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        Optional<Product> productOpt = productRepository.findById(id);
        // Força o carregamento das coleções lazy para evitar LazyInitializationException
        productOpt.ifPresent(product -> {
            product.getPricesHistory().size(); // Força carregamento da collection
            if (product.getProductRecipe().isPresent()) {
                product.getProductRecipe().get().getRecipes().size(); // Força carregamento dos ingredientes
            }
        });
        return productOpt;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        List<Product> products = productRepository.findAll();
        // Força o carregamento das coleções lazy para evitar LazyInitializationException
        products.forEach(product -> {
            product.getPricesHistory().size(); // Força carregamento da collection
            if (product.getProductRecipe().isPresent()) {
                product.getProductRecipe().get().getRecipes().size(); // Força carregamento dos ingredientes
            }
        });
        return products;
    }

    @Transactional(readOnly = true)
    public List<Product> findByNameContainingIgnoreCase(String nameFragment) {
        if (nameFragment == null || nameFragment.trim().isEmpty()) {
            return findAll();
        }
        // Por enquanto, retornar todos os produtos até implementar filtro no repository
        // Aplicar filtro local enquanto não implementamos no repository
        List<Product> allProducts = findAll();
        return allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Product> findByType(ProductType type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo do produto não pode ser nulo");
        }
        List<Product> allProducts = findAll();
        return allProducts.stream()
                .filter(product -> product.getType() == type)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return productRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return productRepository.count();
    }

    // ==================== UPDATE OPERATIONS ====================

    @Transactional
    public Product updateProductName(UUID productId, String newName) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        product.updateName(newName);
        
        return productRepository.save(product);
    }

    @Transactional
    public Product addPrice(UUID productId, Money price) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (price == null) {
            throw new IllegalArgumentException("Preço não pode ser nulo");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        product.addPrice(price);
        return productRepository.save(product);
    }

    @Transactional
    public Product increaseStock(UUID productId, BigDecimal quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        product.increaseStock(quantity);
        return productRepository.save(product);
    }

    @Transactional
    public Product decreaseStock(UUID productId, BigDecimal quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        product.decreaseStock(quantity);
        return productRepository.save(product);
    }

    @Transactional
    public Product defineRecipe(UUID productId, Recipe recipe) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (recipe == null) {
            throw new IllegalArgumentException("Receita não pode ser nula");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        product.defineRecipe(recipe);
        return productRepository.save(product);
    }

    @Transactional
    public Product defineProfitMargin(UUID productId, ProfitMargin profitMargin) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (profitMargin == null) {
            throw new IllegalArgumentException("Margem de lucro não pode ser nula");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        product.defineProfitMargin(profitMargin);
        return productRepository.save(product);
    }

    // ==================== DELETE OPERATIONS ====================

    @Transactional
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void delete(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        productRepository.delete(product);
    }

    // ==================== BUSINESS OPERATIONS ====================

    @Transactional
    public Product addIngredientToRecipe(UUID productId, Product ingredient, Quantity quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingrediente não pode ser nulo");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantidade não pode ser nula");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        if (product.getProductRecipe().isEmpty()) {
            throw new IllegalStateException("Produto não possui receita definida");
        }

        Recipe recipe = product.getProductRecipe().get();
        recipe.addIngredient(ingredient, quantity);
        
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public boolean hasStock(UUID productId, BigDecimal requiredQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        if (requiredQuantity == null || requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + productId));

        return product.getStock().compareTo(requiredQuantity) >= 0;
    }
}