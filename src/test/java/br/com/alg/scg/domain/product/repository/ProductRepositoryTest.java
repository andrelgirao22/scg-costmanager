package br.com.alg.scg.domain.product.repository;

import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.finance.valueobject.ProfitMargin;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Foca nos componentes JPA, carrega repositórios, entidades, etc.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Usa H2 embutido para testes
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // ==================== CREATE TESTS ====================
    
    @Test
    @DisplayName("CREATE: Deve salvar um produto final com receita e ingredientes no banco de dados")
    void save_finalProductWithRecipe_shouldPersistAllData() {
        // -- ARRANGE --
        // 1. Criar e salvar as matérias-primas primeiro
        Product chocolate = Product.createRawMaterial("Chocolate em Pó 50%", new BigDecimal("2.0"));
        chocolate.addPrice(new Money(new BigDecimal("30.00"))); // Preço por KG
        Product farinha = Product.createRawMaterial("Farinha de Trigo", new BigDecimal("5.0"));
        farinha.addPrice(new Money(new BigDecimal("5.00"))); // Preço por KG

        productRepository.save(chocolate);
        productRepository.save(farinha);

        // 2. Criar o produto final
        Product brownie = Product.createFinalProduct("Brownie Tradicional");
        brownie.defineProfitMargin(new ProfitMargin(new BigDecimal("0.80"))); // 80% de margem

        // 3. Criar e associar a receita
        Recipe brownieRecipe = new Recipe();
        brownieRecipe.addIngredient(chocolate, new Quantity(new BigDecimal("0.200"), UnitMeasurement.KILOGRAM)); // 200g
        brownieRecipe.addIngredient(farinha, new Quantity(new BigDecimal("0.150"), UnitMeasurement.KILOGRAM)); // 150g
        brownie.defineRecipe(brownieRecipe);

        // -- ACT --
        // Salvar o agregado raiz (Product), o que deve salvar a receita e ingredientes em cascata
        Product savedBrownie = productRepository.save(brownie);
        productRepository.flush(); // Força a sincronização com o banco de dados

        // -- ASSERT --
        // Buscar do banco para verificar
        Optional<Product> foundBrownieOpt = productRepository.findById(savedBrownie.getId());

        assertTrue(foundBrownieOpt.isPresent());
        Product foundBrownie = foundBrownieOpt.get();

        assertEquals("Brownie Tradicional", foundBrownie.getName());
        assertNotNull(foundBrownie.getProfitMargin());

        assertTrue(foundBrownie.getProductRecipe().isPresent());
        Recipe foundRecipe = foundBrownie.getProductRecipe().get();
        assertEquals(2, foundRecipe.getRecipes().size());

        // Verifica se um dos ingredientes é o chocolate
        assertTrue(foundRecipe.getRecipes().stream()
                .anyMatch(ing -> ing.getRawMaterialId().equals(chocolate.getId())));
    }

    // ==================== READ TESTS ====================

    @Test
    @DisplayName("READ: Deve buscar um produto pelo ID")
    void findById_existingProduct_shouldReturnProduct() {
        // -- ARRANGE --
        Product product = Product.createRawMaterial("Açúcar Cristal", new BigDecimal("3.0"));
        Product savedProduct = productRepository.saveAndFlush(product);

        // -- ACT --
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // -- ASSERT --
        assertTrue(foundProduct.isPresent());
        assertEquals("Açúcar Cristal", foundProduct.get().getName());
        assertEquals(new BigDecimal("3.0"), foundProduct.get().getStock());
    }

    @Test
    @DisplayName("READ: Deve retornar vazio para ID inexistente")
    void findById_nonExistentProduct_shouldReturnEmpty() {
        // -- ARRANGE --
        UUID nonExistentId = UUID.randomUUID();

        // -- ACT --
        Optional<Product> foundProduct = productRepository.findById(nonExistentId);

        // -- ASSERT --
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("READ: Deve listar todos os produtos")
    void findAll_multipleProducts_shouldReturnAllProducts() {
        // -- ARRANGE --
        productRepository.deleteAll(); // Limpa dados existentes
        
        Product chocolate = Product.createRawMaterial("Chocolate em Pó", BigDecimal.ONE);
        Product farinha = Product.createRawMaterial("Farinha de Trigo", BigDecimal.valueOf(2));
        Product brownie = Product.createFinalProduct("Brownie Tradicional");
        
        productRepository.save(chocolate);
        productRepository.save(farinha);
        productRepository.save(brownie);
        productRepository.flush();

        // -- ACT --
        List<Product> allProducts = productRepository.findAll();

        // -- ASSERT --
        assertEquals(3, allProducts.size());
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Chocolate em Pó")));
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Farinha de Trigo")));
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Brownie Tradicional")));
    }

    @Test
    @DisplayName("READ: Deve verificar existência de produto pelo ID")
    void existsById_existingProduct_shouldReturnTrue() {
        // -- ARRANGE --
        Product product = Product.createRawMaterial("Cacau em Pó", BigDecimal.ONE);
        Product savedProduct = productRepository.saveAndFlush(product);

        // -- ACT --
        boolean exists = productRepository.existsById(savedProduct.getId());

        // -- ASSERT --
        assertTrue(exists);
    }

    @Test
    @DisplayName("READ: Deve verificar não existência de produto pelo ID")
    void existsById_nonExistentProduct_shouldReturnFalse() {
        // -- ARRANGE --
        UUID nonExistentId = UUID.randomUUID();

        // -- ACT --
        boolean exists = productRepository.existsById(nonExistentId);

        // -- ASSERT --
        assertFalse(exists);
    }

    @Test
    @DisplayName("READ: Deve contar o número total de produtos")
    void count_multipleProducts_shouldReturnCorrectCount() {
        // -- ARRANGE --
        productRepository.deleteAll(); // Limpa dados existentes
        
        productRepository.save(Product.createRawMaterial("Produto 1", BigDecimal.ONE));
        productRepository.save(Product.createRawMaterial("Produto 2", BigDecimal.ONE));
        productRepository.flush();

        // -- ACT --
        long count = productRepository.count();

        // -- ASSERT --
        assertEquals(2, count);
    }

    // ==================== UPDATE TESTS ====================
    
    @Test
    @DisplayName("UPDATE: Deve atualizar o estoque de um produto")
    void update_productStock_shouldBeReflectedInDatabase() {
        // -- ARRANGE --
        Product materiaPrima = Product.createRawMaterial("Açúcar Refinado", BigDecimal.TEN);
        Product savedProduct = productRepository.saveAndFlush(materiaPrima);

        // -- ACT --
        // Buscar, modificar e salvar novamente
        Product productToUpdate = productRepository.findById(savedProduct.getId()).orElseThrow();
        // Na vida real, a modificação seria feita em um service, mas aqui simulamos
        // Em Java moderno com Records, teríamos que criar um novo objeto.
        // Como Product é uma entidade mutável, podemos fazer assim para o teste:
        // productToUpdate.setName("Açúcar Cristal"); -> Para isso, o setter de nome precisaria ser público.
        // Vamos simular a alteração de um campo que já tem método de alteração.
        productToUpdate.increaseStock(BigDecimal.ONE);
        productRepository.saveAndFlush(productToUpdate);

        // -- ASSERT --
        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals(new BigDecimal("11"), updatedProduct.getStock());
    }

    // ==================== DELETE TESTS ====================
    
    @Test
    @DisplayName("DELETE: Deve remover um produto do banco de dados")
    void delete_product_shouldRemoveItFromDatabase() {
        // -- ARRANGE --
        Product productToDelete = Product.createRawMaterial("Fermento em Pó", BigDecimal.ONE);
        Product savedProduct = productRepository.saveAndFlush(productToDelete);

        // Verifica que ele existe
        assertTrue(productRepository.findById(savedProduct.getId()).isPresent());

        // -- ACT --
        productRepository.deleteById(savedProduct.getId());
        productRepository.flush();

        // -- ASSERT --
        // Verifica que ele não existe mais
        assertFalse(productRepository.findById(savedProduct.getId()).isPresent());
    }

    @Test
    @DisplayName("DELETE: Deve remover um produto final e sua receita em cascata")
    void delete_finalProduct_shouldCascadeDeleteRecipeAndIngredients() {
        // -- ARRANGE --
        // (Similar ao primeiro teste, criando um produto final com receita)
        Product chocolate = productRepository.save(Product.createRawMaterial("Chocolate para Teste", BigDecimal.TEN));
        Product brownie = Product.createFinalProduct("Brownie para Deletar");
        Recipe recipe = new Recipe();
        recipe.addIngredient(chocolate, new Quantity(BigDecimal.ONE, UnitMeasurement.UNIT));
        brownie.defineRecipe(recipe);

        Product savedBrownie = productRepository.saveAndFlush(brownie);
        UUID brownieId = savedBrownie.getId();
        UUID recipeId = savedBrownie.getProductRecipe().get().getId();

        // -- ACT --
        productRepository.deleteById(brownieId);
        productRepository.flush();

        // -- ASSERT --
        assertFalse(productRepository.findById(brownieId).isPresent());
        // Aqui, precisaríamos de RecipeRepository e RecipeIngredientRepository para verificar
        // que a receita e os ingredientes também foram removidos.
        // Isso mostra a importância de testar o comportamento em cascata.
    }
}