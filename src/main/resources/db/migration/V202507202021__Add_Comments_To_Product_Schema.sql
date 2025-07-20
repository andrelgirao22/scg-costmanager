-- V1.1__Add_Comments_To_Product_Schema.sql
-- Este script adiciona comentários descritivos às tabelas e colunas criadas no V1,
-- melhorando a documentação do esquema do banco de dados.

-- =======================================================================================
-- Tabela: recipes
-- =======================================================================================
ALTER TABLE recipes COMMENT = 'Armazena as receitas, que são composições de produtos (matérias-primas).';
ALTER TABLE recipes MODIFY COLUMN id BINARY(16) NOT NULL COMMENT 'ID único da receita (UUIDv7).';


-- =======================================================================================
-- Tabela: products
-- =======================================================================================
ALTER TABLE products COMMENT = 'Armazena tanto matérias-primas quanto produtos finais para venda.';
ALTER TABLE products MODIFY COLUMN id BINARY(16) NOT NULL COMMENT 'ID único do produto (UUIDv7).';
ALTER TABLE products MODIFY COLUMN name VARCHAR(255) NOT NULL COMMENT 'Nome único do produto (ex: "Farinha de Trigo", "Brownie Clássico").';
ALTER TABLE products MODIFY COLUMN type VARCHAR(50) NOT NULL COMMENT 'Tipo do produto (ex: "RAW_MATERIAL", "FINISHED_PRODUCT").';
ALTER TABLE products MODIFY COLUMN stock DECIMAL(10, 3) COMMENT 'Quantidade em estoque (usado principalmente para matérias-primas). A unidade é definida pelo contexto do produto.';
ALTER TABLE products MODIFY COLUMN recipe_id BINARY(16) COMMENT 'FK para a tabela de receitas (recipes), se o produto for composto.';


-- =======================================================================================
-- Tabela: prices
-- =======================================================================================
ALTER TABLE prices COMMENT = 'Mantém um histórico de preços de venda para os produtos.';
ALTER TABLE prices MODIFY COLUMN id BINARY(16) NOT NULL COMMENT 'ID único do registro de preço (UUIDv7).';
ALTER TABLE prices MODIFY COLUMN amount DECIMAL(19, 2) NOT NULL COMMENT 'O valor monetário do preço.';
ALTER TABLE prices MODIFY COLUMN effective_date DATETIME NOT NULL COMMENT 'Data a partir da qual este preço é válido.';
ALTER TABLE prices MODIFY COLUMN product_id BINARY(16) NOT NULL COMMENT 'FK para o produto ao qual este preço se refere.';


-- =======================================================================================
-- Tabela: recipe_ingredients
-- =======================================================================================
ALTER TABLE recipe_ingredients COMMENT = 'Tabela de associação que define os ingredientes e suas quantidades para cada receita.';
ALTER TABLE recipe_ingredients MODIFY COLUMN id BINARY(16) NOT NULL COMMENT 'ID único do ingrediente na receita (UUIDv7).';
ALTER TABLE recipe_ingredients MODIFY COLUMN raw_material_id BINARY(16) NOT NULL COMMENT 'FK para a matéria-prima (um produto) usada na receita.';
ALTER TABLE recipe_ingredients MODIFY COLUMN quantity_value DECIMAL(10, 3) NOT NULL COMMENT 'O valor numérico da quantidade do ingrediente.';
ALTER TABLE recipe_ingredients MODIFY COLUMN quantity_unit VARCHAR(255) NOT NULL COMMENT 'A unidade de medida do ingrediente (ex: "KG", "G", "L", "ML", "UNIT").';
ALTER TABLE recipe_ingredients MODIFY COLUMN recipe_id BINARY(16) NOT NULL COMMENT 'FK para a receita à qual este ingrediente pertence.';