-- V2__Create_Purchases_And_Suppliers_Schema.sql
-- Este script cria as tabelas para os domínios de Compras e Fornecedores
-- e estabelece o relacionamento entre eles.

-- =======================================================================================
--  1. CRIAÇÃO DA TABELA DE FORNECEDORES (suppliers)
-- =======================================================================================
-- Esta tabela é criada primeiro porque 'purchases' terá uma chave estrangeira para ela.
CREATE TABLE suppliers (
    id BINARY(16) NOT NULL COMMENT 'ID único do fornecedor (UUIDv7).',
    name VARCHAR(255) NOT NULL COMMENT 'Nome fantasia ou razão social do fornecedor.',
    document VARCHAR(18) NOT NULL COMMENT 'Documento (CNPJ/CPF) do fornecedor, usado como identificador único de negócio.',

    -- Colunas do Value Object 'Contact' embutido
    email VARCHAR(255) COMMENT 'Endereço de e-mail principal do fornecedor.',
    phone VARCHAR(20) COMMENT 'Número de telefone principal do fornecedor.',

    PRIMARY KEY (id),
    UNIQUE KEY UK_supplier_document (document) COMMENT 'Garante que não haja fornecedores com o mesmo documento.'
) ENGINE=InnoDB COMMENT='Armazena os dados cadastrais dos fornecedores de produtos e matérias-primas.';


-- =======================================================================================
--  2. CRIAÇÃO DA TABELA DE COMPRAS (purchases)
-- =======================================================================================
-- Esta é a tabela raiz do agregado de Compra.
CREATE TABLE purchases (
    id BINARY(16) NOT NULL COMMENT 'ID único da compra (UUIDv7).',
    supplier_id BINARY(16) NOT NULL COMMENT 'FK para o fornecedor associado a esta compra.',
    date DATETIME NOT NULL COMMENT 'Data e hora em que a compra foi registrada.',
    total_cost_amount DECIMAL(19, 2) NOT NULL COMMENT 'Custo total da compra, calculado como a soma dos subtotais dos itens.',

    PRIMARY KEY (id),
    CONSTRAINT FK_purchase_to_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
) ENGINE=InnoDB COMMENT='Armazena o registro de cada compra de produtos ou matérias-primas.';


-- =======================================================================================
--  3. CRIAÇÃO DA TABELA DE ITENS DA COMPRA (purchase_items)
-- =======================================================================================
-- Representa cada linha de produto dentro de uma compra.
CREATE TABLE purchase_items (
    id BINARY(16) NOT NULL COMMENT 'ID único do item da compra (UUIDv7).',

    -- Chaves Estrangeiras
    purchase_id BINARY(16) NOT NULL COMMENT 'FK para a tabela de compras (purchases).',
    product_id BINARY(16) NOT NULL COMMENT 'FK para a tabela de produtos (products).',

    -- Colunas do Value Object 'Quantity'
    quantity_value DECIMAL(10, 3) NOT NULL COMMENT 'O valor numérico da quantidade comprada.',
    quantity_unit VARCHAR(50) NOT NULL COMMENT 'A unidade de medida da quantidade (ex: KG, G, L, ML, UNIT).',

    -- Colunas do Value Object 'Money' para custo unitário
    unit_cost_amount DECIMAL(19, 2) NOT NULL COMMENT 'O custo de uma única unidade do produto nesta compra.',

    -- Colunas do Value Object 'Money' para subtotal
    subtotal_amount DECIMAL(19, 2) NOT NULL COMMENT 'Valor calculado (quantidade * custo_unitário).',

    PRIMARY KEY (id),

    -- Definição das constraints de chave estrangeira
    CONSTRAINT FK_item_to_purchase FOREIGN KEY (purchase_id) REFERENCES purchases (id),
    CONSTRAINT FK_item_to_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB COMMENT='Armazena os detalhes de cada produto dentro de uma compra.';