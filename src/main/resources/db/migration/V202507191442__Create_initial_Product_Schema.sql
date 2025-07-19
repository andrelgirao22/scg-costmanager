-- V1__Create_Initial_Product_Schema.sql
-- Este script cria as tabelas iniciais para o domínio de produtos.

-- Tabela para Receitas (recipes)
-- Criada primeiro porque Product tem uma chave estrangeira para ela.
CREATE TABLE recipes (
    id BINARY(16) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Tabela para Produtos (products)
-- Contém a chave estrangeira para recipes.
CREATE TABLE products (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    stock DECIMAL(10, 3),
    recipe_id BINARY(16),
    PRIMARY KEY (id),
    UNIQUE KEY UK_product_name (name),
    CONSTRAINT FK_product_recipe FOREIGN KEY (recipe_id) REFERENCES recipes (id)
) ENGINE=InnoDB;

-- Tabela para Histórico de Preços (prices)
-- Contém a chave estrangeira para products.
CREATE TABLE prices (
    id BINARY(16) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL, -- Coluna do Value Object 'Money'
    effective_date DATETIME NOT NULL,
    product_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_price_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

-- Tabela para Ingredientes da Receita (recipe_ingredients)
-- Contém chaves estrangeiras para recipes e (logicamente) para products.
CREATE TABLE recipe_ingredients (
    id BINARY(16) NOT NULL,
    raw_material_id BINARY(16) NOT NULL,
    quantity_value DECIMAL(10, 3) NOT NULL, -- Colunas do Value Object 'Quantity'
    quantity_unit VARCHAR(255) NOT NULL,    -- Colunas do Value Object 'Quantity'
    recipe_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_ingredient_recipe FOREIGN KEY (recipe_id) REFERENCES recipes (id)
) ENGINE=InnoDB;