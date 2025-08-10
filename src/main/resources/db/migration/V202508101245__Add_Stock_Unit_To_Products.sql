-- Adiciona coluna de unidade de estoque à tabela products
-- Resolve problema de exibição incorreta "30 kg" para ovos que foram comprados em unidades

ALTER TABLE products
ADD COLUMN stock_unit VARCHAR(20) NULL
COMMENT 'Unidade de medida do estoque (UNIT, KILOGRAM, GRAMA, etc.)';

-- Não definir unidade padrão - será definida na primeira compra
-- UPDATE products SET stock_unit = 'KILOGRAM' WHERE type = 'RAW_MATERIAL' AND stock_unit IS NULL;

-- Comentário explicativo
ALTER TABLE products 
MODIFY COLUMN stock_unit VARCHAR(20) NULL
COMMENT 'Unidade de medida do estoque - definida na primeira compra do produto';