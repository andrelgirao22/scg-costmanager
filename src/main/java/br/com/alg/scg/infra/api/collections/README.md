# API Collections - SCG Cost Manager

Este diretório contém coleções pré-configuradas para testar a API REST do SCG Cost Manager.

## 📁 Arquivos Disponíveis

- `insomnia-collection.json` - Coleção para Insomnia 11.3.0+
- `postman-collection.json` - Coleção para Postman
- `README.md` - Este arquivo de documentação

## 🚀 Como Usar

### Insomnia (v11.3.0+)

1. Abra o Insomnia
2. Vá em **Application** → **Preferences** → **Data** → **Import Data**
3. Selecione o arquivo `insomnia-collection.json`
4. A coleção "SCG Cost Manager API" será importada com todas as requisições organizadas

### Postman

1. Abra o Postman
2. Clique em **Import** no canto superior esquerdo
3. Selecione **Upload Files** e escolha o arquivo `postman-collection.json`
4. A coleção será importada com todas as requisições organizadas por pastas

## 🔧 Variáveis Configuradas

Ambas as coleções vêm com variáveis pré-configuradas:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8080` | URL base da aplicação |
| `api_prefix` | `/api` | Prefixo dos endpoints da API |
| `content_type` | `application/json` | Tipo de conteúdo para requisições |
| `sample_uuid` | `123e4567-e89b-12d3-a456-426614174000` | UUID de exemplo para testes |

### Como Alterar as Variáveis

**Insomnia:**
- Clique no ícone de ambiente (canto superior esquerdo)
- Selecione "Base Environment" 
- Edite os valores conforme necessário

**Postman:**
- Vá em **Variables** na aba da coleção
- Edite os valores nas colunas "Initial Value" e "Current Value"

## 📋 Endpoints Organizados

### 🏷️ Produtos (`/api/products`)
- **POST** Criar Produto
- **GET** Listar Produtos  
- **GET** Buscar Produto por ID
- **DELETE** Excluir Produto
- **GET** Contar Produtos

### 👥 Clientes (`/api/clients`)
- **POST** Criar Cliente
- **GET** Listar Clientes
- **GET** Buscar Cliente por ID
- **DELETE** Excluir Cliente
- **GET** Contar Clientes
- **GET** Listar Clientes Ativos

### 🏭 Fornecedores (`/api/suppliers`)
- **POST** Criar Fornecedor
- **GET** Listar Fornecedores
- **GET** Buscar Fornecedor por ID
- **DELETE** Excluir Fornecedor
- **GET** Contar Fornecedores
- **GET** Buscar Fornecedores por Nome

### 💰 Vendas (`/api/sales`)
- **POST** Criar Venda
- **POST** Adicionar Item à Venda
- **GET** Listar Vendas
- **GET** Buscar Venda por ID
- **DELETE** Excluir Venda
- **GET** Contar Vendas
- **GET** Vendas por Cliente

### 🛒 Compras (`/api/purchases`)
- **POST** Criar Compra
- **POST** Adicionar Item à Compra
- **GET** Listar Compras
- **GET** Buscar Compra por ID
- **DELETE** Excluir Compra
- **GET** Contar Compras
- **GET** Compras por Fornecedor

## 📊 Exemplos de Dados

### Criar Produto
```json
{
  "name": "Chocolate em Pó Premium",
  "initial_stock": 50.00
}
```

### Criar Cliente
```json
{
  "name": "Maria Silva Santos",
  "contact": {
    "email": "maria.santos@email.com",
    "phone": "(11) 99999-9999"
  },
  "delivery_address": {
    "street": "Rua das Flores, 123",
    "city": "São Paulo",
    "postal_code": "01234-567"
  }
}
```

### Criar Fornecedor
```json
{
  "name": "ABC Distribuidora de Chocolates Ltda",
  "document": "12.345.678/0001-90",
  "contact": {
    "email": "vendas@abcdistribuidora.com.br",
    "phone": "(11) 3333-4444"
  }
}
```

### Adicionar Item à Compra
```json
{
  "product_id": "{{sample_uuid}}",
  "quantity": 25.50,
  "unit_measurement": "KG",
  "unit_cost": 12.90
}
```

## ⚠️ Importante

1. **Servidor Local**: Certifique-se de que a aplicação esteja rodando em `http://localhost:8080`
2. **UUIDs Válidos**: Para testar endpoints que requerem IDs, substitua `{{sample_uuid}}` por IDs reais obtidos das requisições de criação
3. **Sequência de Testes**: Para testar fluxos completos:
   - Primeiro crie produtos e clientes
   - Depois crie vendas/compras
   - Por último adicione itens às vendas/compras

## 🔗 Recursos Adicionais

- **Documentação Swagger**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Código Fonte**: Veja os controllers em `src/main/java/br/com/alg/scg/infra/api/controllers/`

## 🐛 Troubleshooting

**Erro de Conexão:**
- Verifique se a aplicação está rodando (`mvn spring-boot:run`)
- Confirme se a porta 8080 está disponível

**Erro 400 (Bad Request):**
- Verifique se o JSON está válido
- Confirme se todos os campos obrigatórios estão preenchidos
- Verifique as validações (email, telefone, CEP, CNPJ)

**Erro 404 (Not Found):**
- Confirme se o endpoint está correto
- Para buscas por ID, use UUIDs válidos retornados pelas operações de criação