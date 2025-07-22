# SCG - COST MANAGER Sistema Gerenciador de custo de Doceria

## Visão Geral

Sistema de controle de vendas e custos para doceria especializada em brownies, desenvolvido seguindo os princípios do Domain-Driven Design (DDD) com Java 21 e Vaadin para interface web.

## Funcionalidades Principais

### 1. Gestão de Produtos
- Cadastro de matérias-primas (chocolate em pó, farinha, açúcar, etc.)
- Cadastro de produtos finais (brownies, tortas, etc.)
- Controle de receitas e composição de produtos
- Gestão de preços por unidade/peso

### 2. Controle de Custos
- Formação de preços baseada em ingredientes
- Cálculo automático de custos por produto
- Margem de lucro configurável
- Histórico de variações de preços

### 3. Gestão de Vendas
- Registro de vendas por produto
- Controle de quantidade vendida
- Cálculo automático de lucro por venda
- Relatórios de vendas por período

### 4. Gestão de Compras
- Registro de compras de matérias-primas
- Controle de estoque
- Histórico de fornecedores
- Alertas de estoque baixo

### 5. Gestão de Clientes
- Cadastro de clientes
- Histórico de compras
- Dados de contato e preferências

## Arquitetura DDD

### Bounded Contexts
- **Produto**: Gestão de produtos, receitas e composições
- **Vendas**: Controle de vendas e relacionamento com clientes
- **Compras**: Gestão de aquisições e fornecedores
- **Financeiro**: Controle de custos, preços e margem de lucro

### Estrutura de Pacotes
```
src/main/java/br/com/alg/scg/
├── domain/
│   ├── product/
│   │   ├── entity/
│   │   ├── valueobject/
│   │   └── repository/
│   ├── purchases/
│   │   └── ...
│   ├── sales/
│   │   └── ...
│   └── finance/
│       └── ...
├── application/
│   ├── usecase/
│   └── dto/
└── infrastructure/
    ├── persistence/
    └── ...
└── presentation/ (ou api)
    └── vaadin/
```

## Tecnologias Utilizadas

- **Java 21**: Linguagem principal com recursos modernos
- **Spring Boot 3.x**: Framework base da aplicação
- **Vaadin 24**: Framework para interface web (UI será implementada posteriormente)
- **JPA/Hibernate**: Persistência de dados
- **Flyway**: Gerenciamento de migrations de banco de dados (schema evolution).
- **MariaDB**: Banco de dados
- **Maven**: Gerenciamento de dependências
- **JUnit 5**: Testes unitários

## Entidades Principais

### Domínio de Produto
- **Produto**: Representa tanto matérias-primas quanto produtos finais
- **Receita**: Define a composição de um produto
- **IngredienteReceita**: Relaciona produtos com quantidades na receita
- **Preco**: Histórico de preços dos produtos

### Domínio de Vendas
- **Venda**: Representa uma transação de venda
- **ItemVenda**: Produtos vendidos em uma venda
- **Cliente**: Dados dos clientes

### Domínio de Compras
- **Compra**: Representa uma aquisição de matérias-primas
- **ItemCompra**: Produtos comprados em uma compra
- **Fornecedor**: Dados dos fornecedores

### Domínio Financeiro
- **CustoProducao**: Custo calculado para produzir um produto
- **MargemLucro**: Margem de lucro configurada por produto
- **RelatorioFinanceiro**: Relatórios de rentabilidade

## Value Objects

- **Dinheiro**: Representa valores monetários
- **Quantidade**: Representa quantidades com unidades
- **Periodo**: Representa períodos de tempo
- **Contato**: Dados de contato (telefone, email, endereço)

## Regras de Negócio Principais

1. **Formação de Preços**: O preço de um produto final é calculado com base nos custos dos ingredientes mais margem de lucro
2. **Controle de Estoque**: Matérias-primas têm controle de estoque, produtos finais são produzidos sob demanda
3. **Histórico de Preços**: Todos os preços são versionados para manter histórico
4. **Validação de Receitas**: Uma receita deve ter pelo menos um ingrediente
5. **Margem Mínima**: Existe uma margem mínima de lucro configurável

## Casos de Uso Principais

1. **Cadastrar Produto**: Criar novos produtos (matéria-prima ou produto final)
2. **Definir Receita**: Criar receita para produtos finais
3. **Calcular Custo**: Calcular custo de produção baseado na receita
4. **Registrar Venda**: Registrar uma nova venda
5. **Registrar Compra**: Registrar compra de matérias-primas
6. **Gerar Relatórios**: Relatórios de vendas, custos e rentabilidade

## Configuração do Projeto

### Pré-requisitos
- Java 21 ou superior
- Maven 3.8+
- IDE com suporte a Java 21

### Executando a Aplicação
```bash
# Clonar o repositório
git clone https://github.com/andrelgirao22/scg-costmanager.git

# Navegar para o diretório
cd scg-costmanager

# Compilar e executar
mvn clean install
mvn spring-boot:run
```

### Testes
```bash
# Executar todos os testes
mvn test

# Executar testes com coverage
mvn test jacoco:report
```

## Roadmap e Próximos Passos

O desenvolvimento está dividido em fases para garantir a construção incremental e consistente da aplicação.

### ✅ Fase 1: Fundação do Domínio e Persistência (Concluído)

- **[✓] Modelagem e Mapeamento JPA - Domínio de `Produto`**:
  - Entidades: `Product`, `Recipe`, `Price`, `RecipeIngredient`.
  - Value Objects: `ProductType`.
  - Migração de banco de dados (`V1`) criada com Flyway.
- **[✓] Modelagem e Mapeamento JPA - Domínio de `Compras`**:
  - Entidades: `Purchase`, `PurchaseItem`, `Supplier`.
  - Value Objects reutilizados: `Contact`, `Money`, `Quantity`.
  - Migração de banco de dados (`V2`) criada com Flyway.
- **[✓] Definição de Value Objects Comuns**:
  - `Money`, `Quantity`, `Contact` e `UnitMeasurement` foram criados como VOs imutáveis e reutilizáveis.
- **[✓] Modelagem e Mapeamento JPA - Domínio de Vendas**:
  - Entidades: Client, Sale (Agregado Raiz), SaleItem.
  - Value Objects reutilizados: Contact, Address, Money.
  - Enums: ClientStatus.
  - Migração de banco de dados (V3) criada com Flyway.
  - -[✓] **Domínio Financeiro**:
  - Definir e mapear entidades e VOs, como `ProfitMargin`.
  - Criar as migrações Flyway correspondentes.

### ➡️ Fase 2: Finalização do Domínio e Camada de Repositório (Em Andamento)

2.  **Implementar os Repositórios (Spring Data JPA):**
  - Criar as interfaces de repositório para cada Agregado Raiz:
    - `ProductRepository`
    - `PurchaseRepository`
    - `SupplierRepository`
    - `SaleRepository` (a ser criado)
    - `ClientRepository` (a ser criado)

### Fase 3: Camada de Aplicação (Casos de Uso)

- **Definir DTOs (Data Transfer Objects):** Criar classes simples no pacote `application/dto` para transportar dados entre as camadas (ex: `CreateProductDTO`, `RegisterPurchaseDTO`).
- **Construir Casos de Uso (Application Services):** Criar serviços que orquestram a lógica da aplicação (ex: `CreateProductUseCase`), utilizando os repositórios e as entidades de domínio.

### Fase 4: Testes

- **Testes de Unidade:** Focar nas regras de negócio das entidades de domínio e nos Value Objects.
- **Testes de Integração:** Validar os fluxos completos, desde os `Application Services` até a persistência no banco de dados, para garantir a integração correta.

### Fase 5: Interface com o Usuário e API

- **Desenvolver a Interface Web:** Criar as telas com Vaadin para interagir com os casos de uso da camada de aplicação.
- **(Opcional) Expor uma API REST:** Criar endpoints para permitir a integração com outros sistemas.

### 6.Segurança
 - **Adicionar autenticação e autorização**

### 7.Deploy
 - **Configurar ambiente de produção**

### 8. Implementar a Camada de Aplicação (`application`)
- **Definir DTOs (Data Transfer Objects):** Criar classes simples no pacote `application/dto` para transportar dados entre a camada de apresentação (Vaadin) e os casos de uso (ex: `CreateProductDTO`).
- **Construir Casos de Uso (Application Services):** Criar classes de serviço (ex: `CreateProductUseCase` ou `ProductApplicationService`) que orquestram a lógica da aplicação.
    - Elas recebem DTOs como entrada.
    - Usam os repositórios para buscar ou salvar entidades do domínio.
    - Invocam os métodos de negócio das entidades.
    - Retornam DTOs ou identificadores como resultado.

### 9. Desenvolver Testes
- **Testes de Unidade:** Focar em testar as regras de negócio nas entidades de domínio e nos *Domain Services* de forma isolada (com Mocks para dependências).
- **Testes de Integração:** Criar testes que validem o fluxo completo, desde o *Application Service* até o banco de dados, para garantir que a persistência e os relacionamentos estão funcionando corretamente.

## Estrutura de Dados

### Principais Relacionamentos
- Um **Produto** pode ter uma **Receita**
- Uma **Receita** tem múltiplos **IngredienteReceita**
- Uma **Venda** tem múltiplos **ItemVenda**
- Uma **Compra** tem múltiplos **ItemCompra**
- Cada **Produto** tem histórico de **Precos**

### Exemplo de Fluxo
1. Cadastrar matérias-primas (chocolate em pó, farinha, etc.)
2. Definir receita para brownie (200g chocolate, 100g farinha, etc.)
3. Sistema calcula custo baseado nos preços das matérias-primas
4. Definir margem de lucro (ex: 40%)
5. Preço final = custo + margem
6. Registrar vendas e acompanhar rentabilidade

## Contribuição

Este projeto é guiado pelos princípios do Domain-Driven Design (DDD), com um foco pragmático para otimizar a produtividade. A principal decisão nesta abordagem é que nossas entidades de domínio também servem como nosso modelo de persistência, sendo diretamente anotadas com JPA.
Essa escolha simplifica o desenvolvimento, reduz o código repetitivo (boilerplate) e mantém a clareza. Apesar disso, o foco principal permanece na modelagem de um domínio rico, com regras de negócio bem encapsuladas e protegidas.