-- V3__Create_Sales_Domain_Tables.sql

-- Tabela para Clientes
CREATE TABLE clients (
    id BINARY(16) NOT NULL,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    delivery_street VARCHAR(255),
    delivery_city VARCHAR(100),
    delivery_postal_code VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    registration_date DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Tabela para Vendas (Agregado Raiz)
CREATE TABLE sales (
    id BINARY(16) NOT NULL,
    client_id BINARY(16) NOT NULL,
    sale_date DATETIME NOT NULL,
    total_value DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_sale_client FOREIGN KEY (client_id) REFERENCES clients (id)
) ENGINE=InnoDB;

-- Tabela para Itens da Venda
CREATE TABLE sale_items (
    id BINARY(16) NOT NULL,
    sale_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    subtotal DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sales (id),
    CONSTRAINT FK_sale_item_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;