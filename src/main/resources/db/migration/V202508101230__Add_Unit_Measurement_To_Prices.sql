-- Adiciona coluna de unidade de medida à tabela de preços para conversão automática
-- Esta funcionalidade permite que compras em kg sejam usadas em receitas com gramas

ALTER TABLE prices 
ADD COLUMN unit_measurement VARCHAR(20) NOT NULL DEFAULT 'KILOGRAM' 
COMMENT 'Unidade de medida do preço (GRAMA, KILOGRAM, LITRO, etc.)';

-- Atualiza valores existentes com a unidade padrão
UPDATE prices SET unit_measurement = 'KILOGRAM' WHERE unit_measurement = 'KILOGRAM';

-- Comentário para clarificar a funcionalidade
ALTER TABLE prices 
MODIFY COLUMN unit_measurement VARCHAR(20) NOT NULL 
COMMENT 'Unidade de medida do preço - permite conversão automática entre kg/g, L/ml, etc.';