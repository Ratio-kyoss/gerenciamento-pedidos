# Documentação Técnica — Sistema de Gerenciamento de Pedidos

## 1. Visão Geral

Sistema web completo para gerenciamento de pedidos, produtos e usuários,
desenvolvido como projeto acadêmico aplicando boas práticas de engenharia
de software.

---

## 2. Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| Java | 21 | Linguagem principal do backend |
| Spring Boot | 4.0.6 | Framework backend |
| Spring Security | 6.x | Autenticação e autorização |
| JWT (jjwt) | 0.12.6 | Tokens de autenticação |
| MySQL | 8.x | Banco de dados relacional |
| Hibernate/JPA | 6.x | Mapeamento objeto-relacional |
| Lombok | 1.18.x | Redução de código boilerplate |
| JUnit 5 | 5.x | Testes unitários |
| Mockito | 5.x | Mocks para testes |
| JaCoCo | 0.8.12 | Cobertura de código |
| k6 | 2.0.0 | Testes de performance |
| HTML/CSS/JS | — | Frontend |
| Maven | 3.9.16 | Gerenciador de dependências |
| Git | 2.54.0 | Versionamento de código |

---

## 3. Arquitetura do Sistema

O projeto segue os princípios da **Clean Architecture** combinada com o
padrão **MVC**, organizada em 4 camadas principais:

┌─────────────────────────────────────┐
│         PRESENTATION                │  Controllers REST + DTOs
├─────────────────────────────────────┤
│         APPLICATION                 │  Casos de Uso (regras de negócio)
├─────────────────────────────────────┤
│         DOMAIN                      │  Entidades + Interfaces
├─────────────────────────────────────┤
│         INFRASTRUCTURE              │  Segurança + Persistência
└─────────────────────────────────────┘

### 3.1 Camada Domain
Contém as entidades do negócio e as interfaces dos repositórios.
Não possui dependências externas — é o núcleo puro da aplicação.

- `User` — usuário do sistema com perfil ADMIN ou USER
- `Product` — produto com nome, preço e controle de estoque
- `Order` — pedido vinculado a um usuário com status e total
- `OrderItem` — item de um pedido com produto e quantidade
- `OperationHistory` — registro de todas as operações realizadas

### 3.2 Camada Application
Contém os casos de uso que implementam as regras de negócio.
Depende apenas do domínio, nunca de frameworks externos.

- `UserUseCase` — cadastro, busca e listagem de usuários
- `ProductUseCase` — CRUD completo e controle de estoque
- `OrderUseCase` — criação, listagem, atualização de status e cancelamento
- `OperationHistoryUseCase` — consulta do histórico de operações

### 3.3 Camada Infrastructure
Implementações concretas de segurança e configurações técnicas.

- `SecurityConfig` — configuração do Spring Security e CORS
- `JwtUtil` — geração e validação de tokens JWT
- `JwtFilter` — filtro que intercepta requisições e valida o token
- `CustomUserDetailsService` — carrega usuário do banco para autenticação
- `DatabaseConfig` — configuração do Spring Data JPA

### 3.4 Camada Presentation
Controllers REST que recebem requisições HTTP e retornam respostas.
Utiliza DTOs para não expor as entidades diretamente.

- `AuthController` — login e cadastro de usuários
- `UserController` — listagem de usuários (ADMIN)
- `ProductController` — CRUD de produtos
- `OrderController` — gerenciamento de pedidos
- `OperationHistoryController` — consulta do histórico

---

## 4. Princípios SOLID Aplicados

### S — Single Responsibility
Cada classe tem uma única responsabilidade. Exemplo: `JwtUtil` apenas
gera e valida tokens, enquanto `JwtFilter` apenas intercepta requisições.

### O — Open/Closed
Os repositórios são interfaces abertas para extensão. Novas
implementações podem ser adicionadas sem modificar o código existente.

### L — Liskov Substitution
`CustomUserDetailsService` implementa `UserDetailsService` do Spring
e pode substituí-la em qualquer contexto sem quebrar o sistema.

### I — Interface Segregation
Os repositórios possuem interfaces específicas por entidade
(`UserRepository`, `ProductRepository`) em vez de uma interface genérica.

### D — Dependency Inversion
Os casos de uso dependem das interfaces dos repositórios, não das
implementações concretas do Spring Data JPA.

---

## 5. Segurança

### Autenticação JWT
1. O usuário envia `username` e `password` para `/api/auth/login`
2. O backend valida as credenciais via Spring Security
3. Se válido, gera um token JWT assinado com HS256
4. O token é enviado ao frontend e armazenado no `localStorage`
5. Todas as requisições subsequentes enviam o token no header:
   `Authorization: Bearer {token}`
6. O `JwtFilter` intercepta e valida o token a cada requisição

### Controle de Acesso por Perfil

| Endpoint | USER | ADMIN |
|----------|------|-------|
| POST /api/auth/** | ✅ | ✅ |
| GET /api/products | ✅ | ✅ |
| POST/PUT/DELETE /api/products | ❌ | ✅ |
| GET/POST /api/orders | ✅ | ✅ |
| PATCH/DELETE /api/orders | ❌ | ✅ |
| /api/users/** | ❌ | ✅ |
| /api/history/** | ❌ | ✅ |

### Proteções implementadas
- Senhas criptografadas com **BCrypt**
- Tokens JWT com expiração de **24 horas**
- CORS configurado para aceitar requisições do frontend
- Sessão **stateless** — sem estado no servidor
- Dependências verificadas contra vulnerabilidades (CVE)

---

## 6. Banco de Dados

### Diagrama de Entidades

users                products
─────────────        ────────────────
id (PK)              id (PK)
username             name
password             description
email                price
role                 stock
created_at           created_at
│
│
orders               order_items
──────────────       ───────────────
id (PK)              id (PK)
user_id (FK)────┐    order_id (FK)──┐
status          │    product_id(FK) │
total_price     │    quantity       │
created_at      │    unit_price     │
updated_at      │                   │
└───────────────────┘
operation_history
─────────────────
id (PK)
operation
entity
entity_id
description
user_id (FK)
created_at

### Estratégia de persistência
- `spring.jpa.hibernate.ddl-auto=update` — tabelas criadas automaticamente
- Relacionamentos mapeados com `@ManyToOne` e `@OneToMany`
- Timestamps automáticos via `@PrePersist` e `@PreUpdate`

---

## 7. Testes

### 7.1 Testes Unitários
Desenvolvidos com **JUnit 5** e **Mockito**, totalizando **16 testes**
organizados em 3 classes:

| Classe | Testes | O que cobre |
|--------|--------|-------------|
| `UserUseCaseTest` | 6 | Criar usuário, duplicidade, busca por ID, listagem |
| `ProductUseCaseTest` | 5 | Criar produto, estoque, deletar, produto inexistente |
| `OrderUseCaseTest` | 5 | Criar pedido, cancelar, estoque insuficiente, status inválido |

Para rodar:
```bash
./mvnw test
```

### 7.2 Cobertura de Código (JaCoCo)
Cobertura total de **50%**, com destaque para:

| Pacote | Cobertura |
|--------|-----------|
| application.usecase | 75% |
| infrastructure.persistence | 100% |
| domain.entity | 67% |
| infrastructure.security | 51% |

Para gerar o relatório:
```bash
./mvnw verify
```
Relatório disponível em: `target/site/jacoco/index.html`

### 7.3 Testes de Performance (k6)
Simulação de **20 usuários simultâneos** por 2 minutos com os seguintes
resultados:

| Métrica | Resultado |
|---------|-----------|
| Total de requisições | 1.350 |
| Taxa de falha | 0% |
| Tempo médio de resposta | 21ms |
| Tempo p(95) | 89ms |
| Checks aprovados | 100% (2160/2160) |

Para rodar:
```bash
k6 run performance/load-test.js
```

---

## 8. Estrutura do Projeto

pedidos/
├── src/
│   ├── main/
│   │   ├── java/com/gerenciamento/pedidos/
│   │   │   ├── domain/
│   │   │   │   ├── entity/         # Entidades JPA
│   │   │   │   └── repository/     # Interfaces dos repositórios
│   │   │   ├── application/
│   │   │   │   └── usecase/        # Regras de negócio
│   │   │   ├── infrastructure/
│   │   │   │   ├── security/       # JWT e Spring Security
│   │   │   │   └── persistence/    # Configuração do banco
│   │   │   └── presentation/
│   │   │       ├── controller/     # Controllers REST
│   │   │       └── dto/            # Objetos de transferência
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/gerenciamento/pedidos/
│           └── usecase/            # Testes unitários
├── frontend/
│   ├── index.html                  # Página de login
│   ├── css/style.css               # Estilos globais
│   ├── js/
│   │   ├── auth.js                 # Gerenciamento de token
│   │   └── api.js                  # Chamadas HTTP
│   └── pages/
│       ├── dashboard.html          # Dashboard principal
│       ├── products.html           # Gestão de produtos
│       └── orders.html             # Gestão de pedidos
├── performance/
│   └── load-test.js                # Testes de performance k6
├── DOCUMENTACAO.md                 # Este arquivo
└── README.md                       # Instruções de execução

---

## 9. Como Executar o Projeto

### Pré-requisitos
- Java JDK 21+
- Maven 3.9+
- MySQL 8+
- k6 2.0+ (para testes de performance)

### Passo a passo

**1. Clonar o repositório**
```bash
git clone https://github.com/Ratio-kyoss/gerenciamento-pedidos.git
cd gerenciamento-pedidos
```

**2. Configurar o banco de dados**
```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```
Edite o arquivo e configure sua senha do MySQL.

**3. Executar o backend**
```bash
./mvnw spring-boot:run
```

**4. Executar o frontend**
Abra `frontend/index.html` com Live Server no VS Code.

**5. Acessar o sistema**
- URL: `http://localhost:5500`
- Crie um usuário ADMIN pelo formulário de cadastro

---

## 10. Endpoints da API

### Autenticação
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | /api/auth/login | Login | ❌ |
| POST | /api/auth/register | Cadastro | ❌ |

### Produtos
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | /api/products | Listar produtos | ✅ |
| POST | /api/products | Criar produto | ADMIN |
| PUT | /api/products/{id} | Atualizar produto | ADMIN |
| DELETE | /api/products/{id} | Excluir produto | ADMIN |
| PATCH | /api/products/{id}/stock | Atualizar estoque | ADMIN |

### Pedidos
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | /api/orders | Listar todos | ✅ |
| GET | /api/orders/my | Meus pedidos | ✅ |
| POST | /api/orders | Criar pedido | ✅ |
| PATCH | /api/orders/{id}/status | Atualizar status | ADMIN |
| DELETE | /api/orders/{id} | Cancelar pedido | ADMIN |

### Histórico
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | /api/history | Listar histórico | ADMIN |
| GET | /api/history/{entity}/{id} | Por entidade | ADMIN |
| GET | /api/history/user/{userId} | Por usuário | ADMIN |

---

## 11. Git e Versionamento

Repositório público disponível em:
**https://github.com/Ratio-kyoss/gerenciamento-pedidos**

### Commits realizados
- `chore:` configuração inicial do projeto
- `feat:` entidades do domínio
- `feat:` repositórios e casos de uso
- `feat:` segurança JWT
- `feat:` controllers REST
- `feat:` frontend HTML/CSS/JS
- `test:` testes unitários com JUnit e Mockito
- `test:` configuração JaCoCo
- `test:` testes de performance com k6
- `docs:` documentação técnica