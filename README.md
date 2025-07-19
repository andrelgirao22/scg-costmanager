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
│   ├── produto/
│   │   ├── entity/
│   │   ├── valueobject/
│   │   ├── repository/
│   │   └── service/
│   ├── vendas/
│   │   ├── entity/
│   │   ├── valueobject/
│   │   ├── repository/
│   │   └── service/
│   ├── compras/
│   │   ├── entity/
│   │   ├── valueobject/
│   │   ├── repository/
│   │   └── service/
│   └── financeiro/
│       ├── entity/
│       ├── valueobject/
│       ├── repository/
│       └── service/
├── application/
│   ├── usecase/
│   └── dto/
├── infrastructure/
│   ├── repository/
│   └── config/
└── presentation/
    └── vaadin/
```

## Tecnologias Utilizadas

- **Java 21**: Linguagem principal com recursos modernos
- **Spring Boot 3.x**: Framework base da aplicação
- **Vaadin 24**: Framework para interface web (UI será implementada posteriormente)
- **JPA/Hibernate**: Persistência de dados
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
git clone [url-do-repositório]

# Navegar para o diretório
cd doceria-sistema

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

## Próximos Passos

1. **Implementação da Persistência**: Configurar JPA repositories
2. **Casos de Uso**: Implementar application services
3. **Interface Web**: Desenvolver telas Vaadin
4. **Relatórios**: Implementar geração de relatórios
5. **Segurança**: Adicionar autenticação e autorização
6. **Deploy**: Configurar ambiente de produção

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

Este projeto segue os princípios do DDD, mantendo o foco no domínio de negócio e separação clara de responsabilidades. Todas as regras de negócio estão encapsuladas no domínio, facilitando manutenção e evolução do sistema.