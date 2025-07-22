-- V4__Add_Profit_Margin_To_Products_Table.sql

ALTER TABLE products
ADD COLUMN profit_margin_percent DECIMAL(5, 4) NULL COMMENT 'Margem de lucro percentual para produtos finais (ex: 0.40 para 40%)';