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
- ✅ Basic validation and error handling implemented
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
- [ ] **Detailed DTO Validations** - Implement comprehensive validation through DTOs
  - [ ] Product validation rules (name, type, stock constraints)
  - [ ] Client validation (email format, phone format, required fields)
  - [ ] Sale/Purchase validation (quantity limits, date validation)
  - [ ] Business rule validations (stock availability, client status)

- [ ] **Advanced Controller Operations** - Additional business endpoints:
  - [ ] Remove items from sales and purchases
  - [ ] Calculate profit margins for sales
  - [ ] Stock management endpoints (increase/decrease stock)
  - [ ] Client management (block/unblock with reason)
  - [ ] Search and filter operations (date ranges, status filters)

- [ ] **Swagger/OpenAPI Documentation** - Complete API documentation:
  - [ ] Configure Swagger dependencies
  - [ ] Document all endpoints with examples
  - [ ] Add response codes and error handling documentation
  - [ ] Create comprehensive API usage guide

- [ ] **API Collections Generation** - Testing tools compatibility:
  - [ ] Generate Insomnia collection
  - [ ] Generate Postman collection
  - [ ] Include sample requests for all endpoints

### Testing Implementation
- [ ] **Unit Tests for Services** - Test business logic in isolation
- [ ] **Integration Tests** - Test complete flows from controller to database
- [ ] **Repository Tests** - Complete testing of all repositories

### Current API Status
- ✅ **ProductController** - Complete CRUD + stock operations
- ✅ **ClientController** - Complete CRUD + status management + search
- ✅ **SupplierController** - Complete CRUD + search by name
- ✅ **SaleController** - Complete CRUD + add items + search by client
- ✅ **PurchaseController** - Complete CRUD + add items + search by supplier