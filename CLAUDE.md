# Claude Code Project Context

## Project Overview
SCG Cost Manager is a bakery cost management system specialized in brownies, built with Domain-Driven Design (DDD) principles using Java 21 and Vaadin for web interface.

## 📋 Task Management
**Para tarefas pendentes detalhadas, consulte**: [TASK.md](./TASK.md)

### Índice de Tarefas
- 📝 **Vaadin Forms Implementation** - Formulários para cadastro (Produto, Cliente, Compra, Venda)
- 🚀 **Advanced Controller Operations** - Funcionalidades avançadas de negócio
- 📋 **API Collections Generation** - Collections para Insomnia/Postman  
- 🧪 **Testing Implementation** - Testes unitários e integração

## Key Technologies
- **Java 21** with Spring Boot 3.x
- **Vaadin 24** for web UI (to be implemented)
- **JPA/Hibernate** for persistence
- **Flyway** for database migrations
- **MariaDB** database

- **Maven** for dependency management
- **JUnit 5** for testing

## Domain Structure (DDD)
### Bounded Contexts
- **Product**: Product management, recipes, compositions
- **Sales**: Sales control and customer relationships  
- **Purchases**: Acquisition and supplier management
- **Financial**: Cost control, pricing, profit margins

### Current Development Phase
**Phase 3: Application Layer (Services, DTOs, Controllers) - COMPLETED**
- ✅ Product domain entities mapped (Product, Recipe, Price, RecipeIngredient)
- ✅ Purchase domain entities mapped (Purchase, PurchaseItem, Supplier) 
- ✅ Sales domain entities mapped (Client, Sale, SaleItem)
- ✅ Financial domain entities mapped (ProfitMargin)
- ✅ Repositories implemented (Spring Data JPA)
- ✅ Application Services created (ProductService, ClientService, SupplierService, PurchaseService, SaleService)
- ✅ Complete DTOs created in infra.api.dto package (including Sales and Purchase DTOs)
- ✅ Complete REST Controllers created in infra.api.controllers with full CRUD operations
- ✅ Business operations implemented (add items to sales/purchases, search by relationships)
- ✅ Jakarta Bean Validation implemented in all DTOs with comprehensive validation rules
- ✅ Complete Swagger/OpenAPI documentation for all REST endpoints
- ✅ DTOMapper for entity-DTO conversion

## Build & Test Commands
```bash
# Build and run
mvn clean install
mvn spring-boot:run

# Tests (SEMPRE usar H2 para testes, não MariaDB)
mvn test -Dspring.profiles.active=test
mvn test jacoco:report -Dspring.profiles.active=test

# IMPORTANTE: Testes devem usar H2 em memória, nunca MariaDB
```

## Key Business Rules
1. Final product pricing = ingredient costs + profit margin
2. Raw materials have stock control, final products are made-to-order
3. All prices are versioned for history tracking
4. Recipes must have at least one ingredient
5. Configurable minimum profit margin

## Project Structure
```
src/main/java/br/com/alg/scg/
├── domain/          # Domain entities and VOs
│   ├── product/     # Product, Recipe entities
│   ├── purchases/   # Purchase, Supplier entities  
│   ├── sales/       # Sale, Client entities
│   ├── finance/     # Financial entities
│   └── common/      # Shared value objects
├── application/     # Services and DTOs
│   └── service/     # Application services
└── infra/          # Infrastructure layer
    ├── api/         # REST controllers and DTOs
    │   ├── controllers/  # REST endpoints
    │   └── dto/         # API data transfer objects
    └── web/         # Vaadin web interface
        ├── layout/      # Main application layout
        └── views/       # UI views by domain
            ├── dashboard/   # Dashboard view
            ├── product/     # Product management UI
            ├── client/      # Client management UI
            ├── sale/        # Sales UI
            └── purchase/    # Purchase management UI
```

## Development Focus
This project uses a pragmatic DDD approach where domain entities are also JPA persistence models to reduce boilerplate while maintaining rich domain modeling with well-encapsulated business rules.

## Phase 4: API Enhancement and Documentation - COMPLETED

## Phase 5: Web Interface with Vaadin - COMPLETED

### ✅ **Vaadin 24 Web Interface** - Complete web application interface
- ✅ **Dependencies** - Vaadin Spring Boot Starter 24.8.3 configured
- ✅ **MainLayout** - Responsive layout with side navigation menu
- ✅ **Dashboard** - Statistics cards and overview of system metrics
- ✅ **Product Management** - CRUD interface with form validation
- ✅ **Client Management** - Complete client management with status badges
- ✅ **Sales View** - Display sales history with client and totals
- ✅ **Purchase View** - Display purchase history with suppliers
- ✅ **Navigation Structure** - Organized menu by domain areas

### **Web Interface Features:**
- 📱 **Responsive Design** - Works on desktop and mobile
- 🎨 **Lumo Theme** - Modern, professional appearance  
- 📊 **Dashboard Cards** - Real-time system statistics
- 🔍 **Search & Filter** - Live filtering in all list views
- ✏️ **Inline Editing** - Click-to-edit functionality
- ✅ **Form Validation** - Client-side validation with error messages
- 🎯 **Context Actions** - Save, delete, cancel operations
- 📱 **Mobile-Friendly** - Drawer navigation for small screens
- 🛒 **Purchase Operations** - Complete purchase creation with supplier management
- 💰 **Sales Operations** - Sales creation interface (price calculation pending)
- 📋 **Expandable Grids** - Detailed item views in purchase/sales history
- ➕ **Inline Entity Creation** - Create suppliers/clients without leaving operation screens
- 🎨 **Consistent Margins** - Standardized padding across all views

### **Available Routes:**
- `/` or `/dashboard` - System overview and metrics
- `/products` - Product management (CRUD)
- `/clients` - Client management (CRUD)  
- `/sales` - Sales history view
- `/sale-operation` - Create new sales with price calculation
- `/purchases` - Purchase history view (with expandable item details)
- `/purchase-operation` - Create new purchases with supplier management

## 🎯 **PRÓXIMAS TAREFAS PRIORITÁRIAS**

### **Phase 6: Sales Operation Interface - IN PROGRESS**
- [x] **🛒 Sales Operation View** - ✅ COMPLETED - Tela para gerar vendas com formação de preço
  - [x] Criar SaleOperationView (/sale-operation) - Interface completa criada
  - [x] Seleção de cliente com botão "Novo Cliente" (similar a compras)
  - [x] Seleção de produtos finais com exibição de preço calculado
  - [x] Grid de itens com quantidades e subtotais
  - [x] Submenu Vendas: "Nova" e "Histórico" implementado
  - [x] ClientDialog para cadastro de novos clientes com endereço
  - [x] Interface responsiva com margins padronizadas
  - [x] Validações de formulário e feedback visual
  - [ ] Cálculo automático baseado em: custo ingredientes + margem de lucro (temporário implementado)
  - [ ] Validação de estoque para matérias-primas necessárias
  - [ ] Integração com SaleService para persistência

- [ ] **💰 Price Calculation Engine** - Motor de cálculo de preços
  - [ ] Implementar cálculo de custo baseado em receitas
  - [ ] Buscar preços mais recentes das matérias-primas (últimas compras)
  - [ ] Aplicar margem de lucro configurável por produto
  - [ ] Exibir breakdown do preço (custo + margem = preço final)
  - [ ] Cache de preços calculados para performance

### Next Development Tasks
- [x] **Detailed DTO Validations** - ✅ COMPLETED - Jakarta Bean Validation implemented
  - [x] Product validation rules (@NotBlank for name, @DecimalMin for stock)
  - [x] Client validation (@Email for email, @Pattern for phone, @NotBlank for required fields)
  - [x] Sale/Purchase validation (@Min for quantities, @PastOrPresent for dates, @NotNull for IDs)
  - [x] Address validation (@Pattern for CEP format)
  - [x] Supplier validation (@Pattern for CNPJ format)
  - [x] Controllers updated with @Valid annotations and manual validations removed

- [x] **Purchase Operation Interface** - ✅ COMPLETED - Complete purchase creation interface
  - [x] PurchaseOperationView with supplier selection and product management
  - [x] Supplier creation dialog integrated
  - [x] Real-time total calculation
  - [x] Items grid with add/remove functionality
  - [x] Purchase history with expandable item details
  - [x] Submenu structure for purchases

- [x] **Sales Operation Interface** - ✅ PARTIALLY COMPLETED - Sales creation interface
  - [x] SaleOperationView (/sale-operation) with client selection
  - [x] ClientDialog for creating new clients with complete address
  - [x] Final products selection (ProductType.FINAL_PRODUCT)
  - [x] Real-time price calculation display (temporary pricing)
  - [x] Items grid with quantity, unit price, and subtotal
  - [x] Submenu structure: "Nova" and "Histórico"
  - [x] Form validations and visual feedback
  - [x] Responsive design with standard margins
  - [ ] **PENDING**: Real price calculation engine based on recipes + profit margin
  - [ ] **PENDING**: Integration with SaleService for persistence
  - [ ] **PENDING**: Stock validation for required raw materials

- [ ] **Advanced Controller Operations** - Additional business endpoints:
  - [ ] Remove items from sales and purchases
  - [ ] Calculate profit margins for sales
  - [ ] Stock management endpoints (increase/decrease stock)
  - [ ] Client management (block/unblock with reason)
  - [ ] Search and filter operations (date ranges, status filters)

- [x] **Swagger/OpenAPI Documentation** - ✅ COMPLETED - Complete API documentation implemented
  - [x] SpringDoc OpenAPI dependency configured (v2.6.0)
  - [x] All controllers documented with @Operation, @ApiResponses, @Parameter annotations
  - [x] Response codes and error handling documented (200, 201, 400, 404)
  - [x] Portuguese descriptions with examples and clear documentation
  - [x] API accessible at /swagger-ui.html and /v3/api-docs

- [ ] **API Collections Generation** - Testing tools compatibility:
  - [ ] Generate Insomnia collection
  - [ ] Generate Postman collection
  - [ ] Include sample requests for all endpoints

### Testing Implementation
- [ ] **Unit Tests for Services** - Test business logic in isolation
- [ ] **Integration Tests** - Test complete flows from controller to database
- [ ] **Repository Tests** - Complete testing of all repositories

### Current API Status
- ✅ **ProductController** - Complete CRUD + stock operations + Swagger documentation
- ✅ **ClientController** - Complete CRUD + status management + search + Swagger documentation  
- ✅ **SupplierController** - Complete CRUD + search by name + Swagger documentation
- ✅ **SaleController** - Complete CRUD + add items + search by client + Swagger documentation
- ✅ **PurchaseController** - Complete CRUD + add items + search by supplier + Swagger documentation

### API Documentation Access
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **API Tags**: Produtos, Clientes, Fornecedores, Vendas, Compras
- **Documentation Language**: Portuguese with comprehensive examples

## Core Business Logic - Key Functionality

### Custo de Fabricação e Precificação
**Principal funcionalidade da aplicação:** Calcular o custo de fabricação de produtos finais baseado no custo das matérias-primas e aplicar margem de lucro para definir o preço de venda.

**Fluxo principal:**
1. **ProductController**: Cadastra produtos (matérias-primas e produtos finais)
   - Matérias-primas: cadastradas com nome e estoque inicial
   - Produtos finais: cadastrados com nome (estoque sempre zero, pois são feitos sob demanda)

2. **PurchaseController**: Registra compras de matérias-primas
   - Aqui é onde as matérias-primas ganham **preço unitário** (preço x, quantidade y)
   - Exemplo: Compra de 10kg de farinha por R$ 50,00 = R$ 5,00/kg

3. **Recipe (Receita)**: Define quais matérias-primas e quantidades são necessárias para o produto final
   - Exemplo: Brownie = 200g farinha + 100g chocolate + 50g açúcar

4. **Cálculo de Custo**: 
   - Custo do brownie = (0,2kg × R$ 5,00/kg farinha) + (0,1kg × R$ 15,00/kg chocolate) + (0,05kg × R$ 3,00/kg açúcar)
   - Custo total = R$ 1,00 + R$ 1,50 + R$ 0,15 = R$ 2,65

5. **Precificação Final**:
   - Custo + Margem de Lucro = Preço de Venda
   - R$ 2,65 + 60% = R$ 4,24

**Resumo dos Controllers:**
- **ProductController**: CRUD de produtos (não define preços, apenas cadastra)
- **PurchaseController**: Define preços das matérias-primas através das compras
- **SaleController**: Registra vendas dos produtos finais pelos preços calculados

## 📋 **Regras de Negócio: Cadastro e Cálculo de Custos**

### **1. Cadastro de Matérias-Primas**
- **Nome**: Cadastrar apenas o nome genérico (ex: "Chocolate em Barra", "Farinha de Trigo")
- **NÃO incluir** peso ou quantidade no nome (❌ "Chocolate 2kg", ✅ "Chocolate em Barra")
- **Tipo**: Sempre "Matéria-Prima"
- **Estoque Inicial**: Geralmente 0 (será incrementado pelas compras)

### **2. Modos de Compra na Interface**

#### **Modo Unitário**
**Quando usar:** Compras onde você já sabe o preço por unidade
- **Exemplo**: Comprou farinha a R$ 5,00/kg
- **Campos**:
  - Quantidade: 10 (kg)
  - Custo Unitário: R$ 5,00 (por kg)
  - Subtotal: R$ 50,00 (calculado automaticamente)

#### **Modo Por Embalagem** 
**Quando usar:** Compras onde você pagou um valor total pela embalagem
- **Exemplo**: Comprou 1 pacote de 2kg de chocolate por R$ 53,09
- **Campos**:
  - Qtd Embalagens: 1
  - Unidades/Embalagem: 2 (kg)
  - Custo da Embalagem: R$ 53,09
  - **Sistema calcula**: R$ 53,09 ÷ 2kg = R$ 26,55/kg

### **3. Fluxo de Cálculo Correto**

#### **Exemplo Prático: Chocolate em Barra**
1. **Cadastro**: 
   - Nome: "Chocolate em Barra"
   - Tipo: Matéria-Prima
   - Estoque: 0 kg

2. **Compra (Modo Por Embalagem)**:
   - Produto: Chocolate em Barra
   - Qtd Embalagens: 1
   - Unidades/Embalagem: 2 kg
   - Custo da Embalagem: R$ 53,09
   - **Resultado**: Estoque = 2kg, Preço = R$ 26,55/kg

3. **Receita do Brownie**:
   - 200g Chocolate em Barra = 0,2kg × R$ 26,55/kg = R$ 5,31
   - 150g Farinha = 0,15kg × R$ 5,00/kg = R$ 0,75
   - **Custo Total**: R$ 6,06

4. **Preço de Venda**:
   - Custo R$ 6,06 + Margem 50% = **R$ 9,09**

### **4. Regras Importantes**

#### **❌ ERROS COMUNS:**
- Cadastrar "Chocolate 2kg" como produto
- No modo unitário, colocar valor total no campo "Custo Unitário"
- Misturar unidades de medida (comprar em kg, usar receita em gramas sem conversão)

#### **✅ PRÁTICAS CORRETAS:**
- Um produto por tipo de matéria-prima (ex: "Açúcar Cristal", "Açúcar Refinado")
- Usar **Modo Por Embalagem** quando comprar pacotes/caixas com preço total
- Usar **Modo Unitário** quando souber o preço por unidade
- Receitas sempre em unidades consistentes (gramas → kg na conversão)

### **5. Vantagens do Sistema**
- **Estoque Real**: Controla quantidade disponível de cada matéria-prima
- **Preço Dinâmico**: Custo atualizado a cada nova compra
- **Cálculo Automático**: Sistema calcula preço de venda baseado nos custos reais
- **Histórico**: Mantém registro de todas as compras e preços pagos