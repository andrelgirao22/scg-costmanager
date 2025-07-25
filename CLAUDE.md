# Claude Code Project Context

## Project Overview
SCG Cost Manager is a bakery cost management system specialized in brownies, built with Domain-Driven Design (DDD) principles using Java 21 and Vaadin for web interface.

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

# Tests
mvn test
mvn test jacoco:report
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
├── domain/
│   ├── product/     # Product, Recipe entities
│   ├── purchases/   # Purchase, Supplier entities  
│   ├── sales/       # Sale, Client entities
│   └── finance/     # Financial entities
├── application/     # Use cases, DTOs
└── infrastructure/  # Persistence, etc.
```

## Development Focus
This project uses a pragmatic DDD approach where domain entities are also JPA persistence models to reduce boilerplate while maintaining rich domain modeling with well-encapsulated business rules.

## Phase 4: API Enhancement and Documentation - PENDING

### Immediate Tasks for Next Session
- [x] **Detailed DTO Validations** - ✅ COMPLETED - Jakarta Bean Validation implemented
  - [x] Product validation rules (@NotBlank for name, @DecimalMin for stock)
  - [x] Client validation (@Email for email, @Pattern for phone, @NotBlank for required fields)
  - [x] Sale/Purchase validation (@Min for quantities, @PastOrPresent for dates, @NotNull for IDs)
  - [x] Address validation (@Pattern for CEP format)
  - [x] Supplier validation (@Pattern for CNPJ format)
  - [x] Controllers updated with @Valid annotations and manual validations removed

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