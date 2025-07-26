# SCG Cost Manager - Tarefas Pendentes

## Status do Projeto
**Fase Atual**: Pós-implementação - Melhorias e Testes
**Última Atualização**: 26/07/2025

## Tarefas Pendentes

### 📝 **Vaadin Forms Implementation**
**Prioridade**: Alta
**Descrição**: Implementar formulários completos para cadastro de entidades na interface web Vaadin.

#### Tarefas:
- [x] **Form para incluir novo produto** ✅ **CONCLUÍDO**
  - ✅ ProductForm com campos para nome, tipo (matéria-prima/produto final), estoque
  - ✅ Validação de campos obrigatórios implementada
  - ✅ Integração com ProductService funcionando
  - ✅ Feedback de sucesso/erro com notificações
  - ✅ Botão "Novo Produto" corrigido e funcionando

- [x] **Form para incluir novo cliente** ✅ **CONCLUÍDO**
  - ✅ ClientForm com campos pessoais (nome, email, telefone, status)
  - ✅ Campos de endereço implementados (rua, cidade, CEP)
  - ✅ Validação integrada com ClientWrapper para binding
  - ✅ Integração com ClientService funcionando
  - ✅ Factory method Client.createFromForm() implementado
  - ✅ Botão "Novo Cliente" corrigido e funcionando
  - ✅ Notificações de sucesso/erro implementadas

- [ ] **Form para incluir uma compra**
  - Criar PurchaseFormView com seleção de fornecedor
  - Implementar grid para adicionar múltiplos itens (produto, quantidade, preço)
  - Calcular total automaticamente
  - Validar estoque e atualizar após salvar

- [ ] **Form para incluir uma venda**
  - Criar SaleFormView com seleção de cliente
  - Implementar grid para adicionar múltiplos itens (produto, quantidade, preço)
  - Calcular total automaticamente
  - Validar disponibilidade de produtos

### 🚀 **Advanced Controller Operations**
**Prioridade**: Alta
**Descrição**: Implementar operações avançadas nos controllers para funcionalidades de negócio adicionais.

#### Tarefas:
- [ ] **Remove items from sales and purchases**
  - Implementar endpoint DELETE para remover itens específicos de vendas
  - Implementar endpoint DELETE para remover itens específicos de compras
  - Validar regras de negócio (não permitir remoção se afetar integridade)

- [ ] **Calculate profit margins for sales**
  - Endpoint para calcular margem de lucro de uma venda específica
  - Endpoint para relatório de margens por período
  - Incluir cálculo baseado no custo das matérias-primas

- [ ] **Stock management endpoints**
  - Endpoint para aumentar estoque (entrada manual)
  - Endpoint para diminuir estoque (saída manual, perdas)
  - Histórico de movimentações de estoque
  - Validações para evitar estoque negativo

- [ ] **Client management advanced operations**
  - Endpoint para bloquear/desbloquear cliente
  - Campo para motivo do bloqueio
  - Histórico de status do cliente
  - Validação para impedir vendas para clientes bloqueados

- [ ] **Search and filter operations**
  - Filtros por data (período) para vendas e compras
  - Filtros por status (ativo/inativo, bloqueado/desbloqueado)
  - Busca avançada com múltiplos critérios
  - Paginação para listagens grandes

### 📋 **API Collections Generation**
**Prioridade**: Média
**Descrição**: Gerar collections para ferramentas de teste de API.

#### Tarefas:
- [ ] **Generate Insomnia collection**
  - Exportar todos os endpoints em formato Insomnia
  - Incluir exemplos de request/response
  - Configurar variáveis de ambiente (base_url, tokens)

- [ ] **Generate Postman collection**
  - Exportar todos os endpoints em formato Postman
  - Incluir testes automatizados básicos
  - Documentar parâmetros e headers necessários

- [ ] **Include sample requests for all endpoints**
  - Dados de exemplo para todos os POSTs e PUTs
  - Cenários de teste (dados válidos e inválidos)
  - Scripts de setup de dados para testes

### 🧪 **Testing Implementation**
**Prioridade**: Alta
**Descrição**: Implementar suíte completa de testes automatizados.

#### Tarefas:
- [ ] **Unit Tests for Services**
  - ProductService: testes de CRUD e regras de negócio
  - ClientService: testes de validação e status
  - SupplierService: testes de busca e validação
  - SaleService: testes de cálculos e adição de itens
  - PurchaseService: testes de atualização de estoque
  - Cobertura mínima: 80%

- [ ] **Integration Tests**
  - Testes end-to-end dos controllers
  - Testes de fluxos completos (compra → estoque → venda)
  - Testes de validação de dados
  - Testes de tratamento de erros

- [ ] **Repository Tests**
  - Testes de todas as queries customizadas
  - Testes de relacionamentos JPA
  - Testes de constraints de banco
  - Testes de performance para queries complexas

## Funcionalidades Completadas ✅

### Phase 1-3: Domain, Application & Controllers
- ✅ Domain entities com DDD
- ✅ Application Services completos
- ✅ REST Controllers com CRUD completo
- ✅ DTOs com validação Jakarta Bean Validation
- ✅ Swagger/OpenAPI documentation

### Phase 4-5: API & Web Interface
- ✅ Documentação Swagger completa
- ✅ Interface web Vaadin 24
- ✅ Layout responsivo com navegação
- ✅ Views para todas as entidades
- ✅ Dashboard com métricas

## Próximos Passos Recomendados

1. **Prioridade Imediata**: Advanced Controller Operations (funcionalidades core de negócio)
2. **Segundo**: Testing Implementation (garantia de qualidade)
3. **Terceiro**: API Collections (facilitar desenvolvimento e QA)

## Comandos de Build e Teste
```bash
# Build e execução
mvn clean install
mvn spring-boot:run

# Testes (quando implementados)
mvn test
mvn test jacoco:report

# Acessar aplicação
http://localhost:8080          # Interface web
http://localhost:8080/swagger-ui.html  # Documentação API
```

## Estimativa de Tempo
- **Vaadin Forms Implementation**: ~~2-3 dias~~ → **1 dia restante** (Product form ✅ concluído)
- **Advanced Controller Operations**: 2-3 dias
- **Testing Implementation**: 3-4 dias  
- **API Collections**: 1 dia

**Total estimado**: ~~8-11 dias~~ → **7-9 dias de desenvolvimento**

## Progresso
- ✅ **Form para incluir novo produto** - Concluído e funcionando
- ✅ **Form para incluir novo cliente** - Concluído e funcionando
- 🔄 **Próximos**: Forms para Compra e Venda