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
├── domain/                    # Camada de Domínio (DDD)
│   ├── product/
│   │   ├── entity/           # Product, Recipe, Price, RecipeIngredient
│   │   ├── valueobject/      # ProductType
│   │   └── repository/       # ProductRepository
│   ├── purchases/
│   │   ├── entity/           # Purchase, PurchaseItem, Supplier
│   │   └── repository/       # PurchaseRepository, SupplierRepository
│   ├── sales/
│   │   ├── entity/           # Sale, SaleItem, Client
│   │   ├── valueobject/      # ClientStatus
│   │   └── repository/       # SaleRepository, ClientRepository
│   ├── finance/
│   │   └── valueobject/      # ProfitMargin
│   └── common/
│       └── valueobject/      # Money, Quantity, Contact, Address
├── application/               # Camada de Aplicação
│   └── service/              # ProductService, ClientService, etc.
└── infra/                    # Camada de Infraestrutura
    ├── api/                  # REST API
    │   ├── controllers/      # REST Controllers
    │   ├── dto/              # DTOs organizados por domínio
    │   │   ├── client/       # ClientDTO, CreateClientDTO
    │   │   ├── product/      # ProductDTO, CreateProductDTO
    │   │   ├── sale/         # SaleDTO, CreateSaleDTO
    │   │   ├── purchase/     # PurchaseDTO, CreatePurchaseDTO
    │   │   ├── supplier/     # SupplierDTO, CreateSupplierDTO
    │   │   └── common/       # ContactDTO, AddressDTO
    │   ├── exception/        # Exception handlers
    │   └── validation/       # Custom validators
    ├── persistence/          # Repositórios JPA (implementações)
    └── web/                  # Interface Web Vaadin
        ├── layout/           # MainLayout
        └── views/            # Telas da aplicação
            ├── dashboard/    # DashboardView
            ├── client/       # ClientView, ClientForm
            ├── product/      # ProductView, ProductForm
            ├── supplier/     # SupplierView, SupplierForm
            ├── sale/         # SaleView, SaleForm
            └── purchase/     # PurchaseView, PurchaseForm

src/main/resources/
├── META-INF/resources/
│   └── themes/scg-theme/
│       └── shared-styles.css  # CSS personalizado
├── db/migration/              # Flyway migrations
│   ├── V1__create_product_tables.sql
│   ├── V2__create_purchase_tables.sql
│   └── V3__create_sales_tables.sql
└── application.properties     # Configurações da aplicação
```

## Tecnologias Utilizadas

- **Java 21**: Linguagem principal com recursos modernos
- **Spring Boot 3.x**: Framework base da aplicação
- **Spring Boot Validation**: Jakarta Bean Validation para validação de dados
- **SpringDoc OpenAPI**: Documentação automática da API REST
- **Vaadin 24.8.3**: Framework para interface web completa
- **JPA/Hibernate**: Persistência de dados
- **Flyway**: Gerenciamento de migrations de banco de dados (schema evolution)
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

A aplicação estará disponível em:
- **Interface Web (Vaadin)**: `http://localhost:8080`
- **API REST Swagger**: `http://localhost:8080/swagger-ui.html`
- **Documentação OpenAPI**: `http://localhost:8080/v3/api-docs`

### Testes
```bash
# Executar todos os testes
mvn test

# Executar testes com coverage
mvn test jacoco:report
```

## Interface Web (Vaadin)

### Telas Implementadas

A aplicação possui uma interface web completa desenvolvida com **Vaadin 24.8.3**, oferecendo uma experiência de usuário moderna e responsiva:

#### **Dashboard Principal** (`/dashboard` ou `/`)
- **Visão geral do sistema** com cards estatísticos em tempo real
- **Métricas principais**: Total de produtos, clientes, vendas e compras
- **Design responsivo** com cards coloridos e ícones intuitivos
- **Seção de gráficos** preparada para futuras implementações

#### **Gestão de Produtos** (`/products`)
- **CRUD completo** de produtos (matérias-primas e produtos finais)
- **Filtro de busca** por nome do produto
- **Grid interativo** com colunas: Nome, Tipo, Estoque, Preço Atual
- **Formulário lateral** para criação/edição com:
  - Seleção do tipo de produto (Matéria-Prima/Produto Final)
  - Campo de estoque (apenas para matérias-primas)
  - Validações automáticas de campos obrigatórios

#### **Gestão de Clientes** (`/clients`)
- **CRUD completo** de clientes com informações de contato
- **Filtro de busca** por nome ou email
- **Grid com status badges** (Ativo/Inativo/Bloqueado)
- **Formulário completo** incluindo:
  - Dados pessoais (nome, email, telefone)
  - Endereço de entrega (CEP, rua, cidade, estado)
  - Status do cliente com seleção via dropdown
  - Validações de formato (email, telefone, CEP)

#### **Gestão de Fornecedores** (`/suppliers`)
- **CRUD completo** de fornecedores
- **Filtro de busca** por nome
- **Informações empresariais**: CNPJ, razão social, contato
- **Formulário com validações** de CNPJ e dados obrigatórios

#### **Gestão de Vendas** (`/sales`)
- **Listagem completa** de vendas realizadas
- **Detalhes de cada venda**: cliente, data, valor total, itens
- **Interface para criar novas vendas**
- **Adição de itens** com seleção de produtos e quantidades

#### **Gestão de Compras** (`/purchases`)
- **Listagem completa** de compras de matérias-primas
- **Detalhes de cada compra**: fornecedor, data, valor total, itens
- **Interface para registrar novas compras**
- **Controle de estoque** através das compras registradas

### Características da Interface Web

#### **Design e Usabilidade**
- **Layout responsivo** com navegação lateral retrátil
- **Tema Lumo** do Vaadin com personalização CSS
- **Ícones consistentes** da biblioteca VaadinIcon
- **Cards estatísticos** com cores temáticas por módulo
- **Grid interativo** com ordenação e seleção de linhas
- **Formulários laterais** que abrem/fecham suavemente

#### **Navegação e Estrutura**
- **MainLayout** como estrutura base com:
  - Header com logo e título da aplicação
  - Menu lateral com navegação por módulos
  - Área de conteúdo principal responsiva
- **Roteamento automático** entre as telas
- **Breadcrumb visual** através dos títulos das páginas

#### **Funcionalidades Técnicas**
- **Binding automático** entre formulários e entidades
- **Validação em tempo real** nos formulários
- **Tratamento de erros** com logs detalhados
- **Factory methods** para criação segura de entidades
- **@PrePersist hooks** para inicialização automática
- **Integração completa** com os Application Services

#### **Arquivos de Estilo**
- **CSS personalizado** (`shared-styles.css`) com:
  - Estilos para cards estatísticos
  - Hover effects e transições
  - Responsive design para diferentes telas
  - Badges de status personalizados
  - Grid styling aprimorado

### Integração com Backend

A interface web está **completamente integrada** com:
- **Application Services** para lógica de negócio
- **Entidades de domínio** através de factory methods
- **Validações Jakarta** para consistência de dados
- **Repositórios JPA** para persistência
- **Value Objects** para tipos complexos (Contact, Address, Money)

### Estrutura de Arquivos Web

```
src/main/java/br/com/alg/scg/infra/web/
├── layout/
│   └── MainLayout.java           # Layout principal
├── views/
│   ├── dashboard/
│   │   └── DashboardView.java    # Tela inicial
│   ├── client/
│   │   ├── ClientView.java       # Listagem de clientes
│   │   └── ClientForm.java       # Formulário de cliente
│   ├── product/
│   │   ├── ProductView.java      # Listagem de produtos
│   │   └── ProductForm.java      # Formulário de produto
│   ├── supplier/
│   │   ├── SupplierView.java     # Listagem de fornecedores
│   │   └── SupplierForm.java     # Formulário de fornecedor
│   ├── sale/
│   │   └── SaleView.java         # Gestão de vendas
│   └── purchase/
│       └── PurchaseView.java     # Gestão de compras
└── components/                   # Componentes reutilizáveis

src/main/resources/META-INF/resources/
└── themes/scg-theme/
    └── shared-styles.css         # Estilos personalizados
```

### Padrões de Desenvolvimento

- **Factory Methods**: Para criação segura de entidades
- **@PrePersist**: Para inicialização automática de campos
- **Event-Driven**: Comunicação entre componentes via eventos
- **Service Integration**: Injeção de dependência dos Application Services
- **Form Binding**: Ligação automática entre formulários e entidades

## API REST e Documentação

### Endpoints Disponíveis

A aplicação possui uma API REST completa com os seguintes recursos:

#### **Produtos** (`/api/products`)
- `POST /api/products` - Criar novo produto
- `GET /api/products` - Listar todos os produtos
- `GET /api/products/{id}` - Buscar produto por ID
- `DELETE /api/products/{id}` - Excluir produto
- `GET /api/products/count` - Contar produtos

#### **Clientes** (`/api/clients`)
- `POST /api/clients` - Criar novo cliente
- `GET /api/clients` - Listar todos os clientes
- `GET /api/clients/{id}` - Buscar cliente por ID
- `DELETE /api/clients/{id}` - Excluir cliente
- `GET /api/clients/count` - Contar clientes
- `GET /api/clients/active` - Listar clientes ativos

#### **Fornecedores** (`/api/suppliers`)
- `POST /api/suppliers` - Criar novo fornecedor
- `GET /api/suppliers` - Listar todos os fornecedores
- `GET /api/suppliers/{id}` - Buscar fornecedor por ID
- `DELETE /api/suppliers/{id}` - Excluir fornecedor
- `GET /api/suppliers/count` - Contar fornecedores
- `GET /api/suppliers/search?name={nome}` - Buscar fornecedores por nome

#### **Vendas** (`/api/sales`)
- `POST /api/sales` - Criar nova venda
- `GET /api/sales` - Listar todas as vendas
- `GET /api/sales/{id}` - Buscar venda por ID
- `DELETE /api/sales/{id}` - Excluir venda
- `POST /api/sales/{saleId}/items` - Adicionar item à venda
- `GET /api/sales/count` - Contar vendas
- `GET /api/sales/client/{clientId}` - Buscar vendas por cliente

#### **Compras** (`/api/purchases`)
- `POST /api/purchases` - Criar nova compra
- `GET /api/purchases` - Listar todas as compras
- `GET /api/purchases/{id}` - Buscar compra por ID
- `DELETE /api/purchases/{id}` - Excluir compra
- `POST /api/purchases/{purchaseId}/items` - Adicionar item à compra
- `GET /api/purchases/count` - Contar compras
- `GET /api/purchases/supplier/{supplierId}` - Buscar compras por fornecedor

### Documentação Swagger/OpenAPI

A API possui documentação completa e interativa acessível através do Swagger UI:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

#### Características da Documentação:
- **Descrições em português** com exemplos claros
- **Códigos de resposta HTTP** documentados (200, 201, 400, 404)
- **Exemplos de UUIDs** para facilitar testes
- **Validações documentadas** com mensagens de erro
- **Tags organizadas** por domínio: Produtos, Clientes, Fornecedores, Vendas, Compras

### Validações da API

Todos os DTOs possuem validações Jakarta Bean Validation:

- **Campos obrigatórios**: `@NotBlank`, `@NotNull`
- **Validação de email**: `@Email` com formato válido
- **Validação de telefone**: `@Pattern` para formato brasileiro
- **Validação de CEP**: `@Pattern` para formato brasileiro
- **Validação de CNPJ**: `@Pattern` para formato válido
- **Validações numéricas**: `@DecimalMin`, `@Min` para valores monetários e quantidades
- **Validações de data**: `@PastOrPresent` para datas de compras
- **Validação de objetos aninhados**: `@Valid` para ContactDTO e AddressDTO

#### Exemplo de Mensagens de Erro:
```json
{
  "timestamp": "2025-01-25T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Nome do produto é obrigatório"
    },
    {
      "field": "contact.email",
      "message": "Email deve ter formato válido"
    }
  ]
}
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

### ✅ Fase 2: Finalização do Domínio e Camada de Repositório (Concluído)

- **[✓] Implementar os Repositórios (Spring Data JPA):**
  - Criar as interfaces de repositório para cada Agregado Raiz:
    - `ProductRepository`
    - `PurchaseRepository`
    - `SupplierRepository`
    - `SaleRepository`
    - `ClientRepository`

### ✅ Fase 3: Camada de Aplicação e API REST (Concluído)

- **[✓] Definir DTOs (Data Transfer Objects):** Criadas classes completas no pacote `infra/api/dto`:
  - **Produto**: `ProductDTO`, `CreateProductDTO`
  - **Cliente**: `ClientDTO`, `CreateClientDTO`
  - **Fornecedor**: `SupplierDTO`, `CreateSupplierDTO`
  - **Venda**: `SaleDTO`, `SaleItemDTO`, `CreateSaleDTO`, `AddSaleItemDTO`
  - **Compra**: `PurchaseDTO`, `PurchaseItemDTO`, `CreatePurchaseDTO`, `AddPurchaseItemDTO`
  - **Value Objects**: `ContactDTO`, `AddressDTO`
  - **Mapper**: `DTOMapper` para conversão entre entidades e DTOs
- **[✓] Construir Services de Aplicação:** Criados serviços completos:
  - `ProductService`, `ClientService`, `SupplierService`, `PurchaseService`, `SaleService`
  - Operações CRUD completas + validações de negócio + gestão de estoque
- **[✓] Criar API REST Completa:** Implementados todos os controllers:
  - `ProductController`, `ClientController`, `SupplierController`
  - `SaleController`, `PurchaseController`
  - Endpoints para CRUD + operações de negócio (adicionar itens, buscar por relacionamentos)
  - Validações básicas implementadas + tratamento de erros
  - Localização: `infra/api/controllers`
- **[✓] Implementar Validações Jakarta Bean Validation:**
  - Validações completas em todos os DTOs (@NotBlank, @Email, @Pattern, @DecimalMin, etc.)
  - Validação de CEP, telefone, email e CNPJ com regex patterns
  - Anotação @Valid nos controllers para validação automática
  - Mensagens de erro personalizadas em português
- **[✓] Documentação Swagger/OpenAPI Completa:**
  - SpringDoc OpenAPI dependency configurada (v2.6.0)
  - Todos os controllers documentados com @Operation, @ApiResponses, @Parameter
  - Códigos de resposta HTTP documentados (200, 201, 400, 404)
  - Descrições em português com exemplos de UUIDs
  - Interface acessível via `/swagger-ui.html` e `/v3/api-docs`

### ✅ Fase 4: Interface Web Vaadin (Concluído)

- **[✓] Estrutura Base da Interface Web:**
  - MainLayout com navegação lateral responsiva
  - Configuração do Vaadin 24.8.3 no projeto Maven
  - CSS personalizado (shared-styles.css) para styling avançado
  - Integração completa com Application Services
- **[✓] Dashboard Principal:**
  - Cards estatísticos em tempo real (produtos, clientes, vendas, compras)
  - Design responsivo com HorizontalLayout (substituindo Board)
  - Seção preparada para futuros gráficos e métricas
- **[✓] Gestão de Produtos (ProductView/ProductForm):**
  - CRUD completo com grid interativo
  - Formulário com seleção de tipo (Matéria-Prima/Produto Final)
  - Controle de estoque e preços
  - Factory method `createFromForm()` para inicialização segura
- **[✓] Gestão de Clientes (ClientView/ClientForm):**
  - Interface completa com dados pessoais e endereço
  - Status badges (Ativo/Inativo/Bloqueado)
  - Validações de email, telefone e CEP
  - @PrePersist para inicialização automática de ID e status
- **[✓] Gestão de Fornecedores (SupplierView/SupplierForm):**
  - CRUD com informações empresariais
  - Validação de CNPJ e dados obrigatórios
  - Busca por nome do fornecedor
- **[✓] Gestão de Vendas e Compras:**
  - Views básicas implementadas
  - Integração com services existentes
  - Preparadas para expansão futura
- **[✓] Correções Técnicas:**
  - Resolvidos problemas de encoding em application.properties
  - Factory methods implementados para contornar construtores protegidos
  - Compilação completa sem erros
  - Integração bem-sucedida entre camada web e domain

### ➡️ Fase 5: Expansão da API e Melhorias (Próxima)

- **[ ] Operações avançadas nos Controllers:**
  - Remover itens de vendas e compras
  - Cálculo de margens de lucro
  - Gerenciamento de estoque via endpoints (aumentar/diminuir estoque)
  - Filtros e buscas avançadas (por data, status, etc.)
  - Gerenciamento de clientes (bloquear/desbloquear com motivo)
- **[ ] Gerar Collections de API:**
  - Collections compatíveis com Insomnia
  - Collections compatíveis com Postman
  - Incluir exemplos de requisições para todos os endpoints

### Fase 6: Aprimoramentos da Interface Web

- **[ ] Melhorias na Interface:**
  - Implementar notificações de sucesso/erro nas operações CRUD
  - Adicionar confirmação de exclusão com diálogos
  - Implementar busca avançada com múltiplos filtros
  - Adicionar paginação nos grids para melhor performance
- **[ ] Funcionalidades Avançadas:**
  - Tela de receitas com gestão de ingredientes
  - Calculadora de custos em tempo real
  - Relatórios visuais com gráficos (Chart.js ou similar)
  - Export de dados (Excel, PDF)
- **[ ] Gestão de Vendas/Compras Avançada:**
  - Interface para adicionar/remover itens
  - Cálculo automático de totais
  - Histórico de transações por cliente/fornecedor
  - Status de pagamento e controle financeiro

### Fase 7: Testes

- **Testes de Unidade:** Focar nas regras de negócio das entidades de domínio e nos Value Objects.
- **Testes de Integração:** Validar os fluxos completos, desde os `Application Services` até a persistência no banco de dados, para garantir a integração correta.
- **Testes de Interface:** Testes automatizados das telas Vaadin com TestBench

### Fase 8: Segurança e Autenticação

- **[ ] Implementar Spring Security:**
  - Autenticação baseada em sessão para interface web
  - Proteção de endpoints REST com JWT
  - Controle de acesso por perfis (admin, operador, consulta)
- **[ ] Gestão de Usuários:**
  - Tela de login e logout
  - Cadastro de usuários do sistema
  - Perfis de permissão por funcionalidade

### Fase 9: Deploy e Produção

- **[ ] Configuração de Ambiente:**
  - Dockerfile para containerização
  - Docker Compose com MariaDB
  - Configurações de perfis (dev, prod)
  - Scripts de inicialização e migrations
- **[ ] Monitoramento:**
  - Health checks e métricas
  - Logs estruturados
  - Backup automatizado do banco

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