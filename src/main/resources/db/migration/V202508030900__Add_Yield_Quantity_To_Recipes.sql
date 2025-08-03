-- Adiciona campo de rendimento (yield_quantity) na tabela recipes
-- Para calcular custo unitário: custo_total ÷ rendimento = custo_por_unidade

ALTER TABLE recipes 
ADD COLUMN yield_quantity DECIMAL(10,3) NOT NULL DEFAULT 1.000 
COMMENT 'Quantidade de unidades que a receita produz (ex: 7 brownies)';

-- Atualizar receitas existentes com rendimento padrão de 1 unidade
UPDATE recipes SET yield_quantity = 1.000 WHERE yield_quantity IS NULL;