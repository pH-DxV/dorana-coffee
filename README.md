# Dorana Coffee Shop (LGC API) — Plataforma & API RESTful de Cafés Especiais

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-3.26.3-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-21_(SSR)-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/Security-SmallRye_JWT_(RSA)-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

API RESTful corporativa e aplicação Full Stack desenvolvidas para o comércio eletrônico e gestão de torrefação da **Dorana Coffee Shop** (*LGC — E-commerce de Grãos de Café*). O sistema foi projetado especificamente para o mercado de **cafés especiais e microlotes (SCA 80–100 pontos)**, contemplando rastreabilidade sensorial, controle de estoque com bloqueio otimista de concorrência, múltiplos endereços por cliente, processamento polimórfico de pagamentos e vitrine integrada em Angular 21.

---

## Sumário

1. [Visão Geral e Arquitetura do Projeto](#1-visão-geral-e-arquitetura-do-projeto)
2. [Peculiaridades Técnicas e Decisões de Engenharia da API](#2-peculiaridades-técnicas-e-decisões-de-engenharia-da-api)
3. [Modelo de Domínio e Enums Especializados](#3-modelo-de-domínio-e-enums-especializados)
4. [Segurança, Autenticação JWT e Controle de Propriedade (RBAC + Ownership)](#4-segurança-autenticação-jwt-e-controle-de-propriedade-rbac--ownership)
5. [Padronização de Erros (RFC 7807 — Problem Details)](#5-padronização-de-erros-rfc-7807--problem-details)
6. [Catálogo Completo de Endpoints da API](#6-catálogo-completo-de-endpoints-da-api)
7. [Requisitos e Configuração do Ambiente](#7-requisitos-e-configuração-do-ambiente)
8. [Como Executar (Backend Quarkus, Frontend Angular e Testes)](#8-como-executar-backend-quarkus-frontend-angular-e-testes)
9. [Credenciais Padrão de Desenvolvimento (Seed Data)](#9-credenciais-padrão-de-desenvolvimento-seed-data)
10. [Exemplos Práticos de Requisições (cURL)](#10-exemplos-práticos-de-requisições-curl)

---

## 1. Visão Geral e Arquitetura do Projeto

O repositório segue uma organização monorepo limpa na pasta `dorana/`, separando claramente o backend Quarkus, o frontend Angular e a documentação de engenharia e auditoria:

```text
coffehouse/
├── docs/                                # Documentação técnica, UML, auditorias e prompts UI/UX
└── dorana/                              # Diretório principal do projeto
    ├── pom.xml                          # Build Maven configurado para apontar para backend/src
    ├── mvnw / mvnw.cmd                  # Maven Wrapper
    ├── backend/
    │   └── src/
    │       ├── main/java/br/unitins/topicos1/lgc/
    │       │   ├── auth/                # Autenticação (/auth/login) e DTOs de Token
    │       │   ├── cafe/                # Catálogo de Cafés, Notas Sensoriais, Torra e Tratamento
    │       │   ├── categoriaDoCafe/     # Categorias sensoriais e linhas de microlotes
    │       │   ├── marca/               # Marcas e torrefações parceiras
    │       │   ├── estoque/             # Entidade Estoque isolada com controle de concorrência (@Version)
    │       │   ├── pedido/              # Processamento de pedidos, cálculo de frete e cancelamento
    │       │   ├── itemPedido/          # Itens e snapshot de preço unitário histórico
    │       │   ├── pagamento/           # Herança polimórfica (PIX, Cartão de Crédito e Boleto)
    │       │   ├── usuario/             # Gestão administrativa de usuários e perfil logado (/me)
    │       │   ├── cliente/             # Auto-cadastro e gestão de clientes
    │       │   ├── administrador/       # Entidade especializada de administradores
    │       │   ├── endereco/            # Múltiplos endereços de entrega por usuário
    │       │   ├── telefone/            # Múltiplos telefones por usuário
    │       │   ├── estado/              # Unidades Federativas e Macrorregiões (JPA Converter)
    │       │   ├── municipio/           # Municípios vinculados aos Estados
    │       │   ├── regiaoDeOrigem/      # Entidade de Terroir (Altitude, Clima, Estado/Município)
    │       │   ├── security/            # JwtService, HashService (SHA-512) e SecurityService
    │       │   └── exception/           # RFC 7807 Problem Details e ExceptionMappers globais
    │       ├── main/resources/
    │       │   ├── application.properties
    │       │   ├── import.sql           # Carga inicial (Estados, Municípios, Admin, Cliente, Cafés, Estoque)
    │       │   └── token/               # Par de chaves assimétricas RSA (privateKey.pem / publicKey.pem)
    │       └── test/java/               # 13 suítes de testes de integração REST Assured + JUnit 5
    └── frontend/                        # E-commerce & Backoffice em Angular 21 (SSR + Design "Torrefação Nobre")
        └── src/app/
```

---

## 2. Peculiaridades Técnicas e Decisões de Engenharia da API

Esta API possui características arquiteturais próprias que devem ser observadas por integradores e desenvolvedores frontend:

### 2.1. Convenção de Pacotes por Domínio Funcional (`lowerCamelCase`)
Diferente da divisão tradicional em pacotes genéricos (`controller`, `service`, `repository`), o código-fonte do backend é modularizado **por domínio de negócio** em `lowerCamelCase` (`categoriaDoCafe`, `itemPedido`, `regiaoDeOrigem`, `cafe`, `pedido`, `pagamento`, etc.). Cada pacote encapsula sua própria entidade JPA, DTOs de entrada/saída, `PanacheRepository`, interface/implementação de `Service` e `Resource` REST.

### 2.2. Separação entre `Cafe` e `Estoque` com Bloqueio Otimista (`@Version`)
Embora o contrato de entrada/saída do café (`CafeDTO` e `CafeDTOResponse`) exponha o campo `estoque` de forma transparente para facilitar o consumo pelo frontend, no banco de dados o saldo não fica na tabela `cafe`:
- Existe uma entidade dedicada **`Estoque`** (`tabela estoque`) associada `1:1` ao `Cafe`.
- A entidade `Estoque` utiliza **`@Version` do Jakarta Persistence (Optimistic Locking)** e registra `dataUltimaMovimentacao`.
- Caso dois clientes tentem comprar as últimas unidades do mesmo microlote simultaneamente, o controle de versão impede *race conditions* (venda duplicada sem saldo).
- **Exclusão Lógica (Soft Delete):** A entidade `Cafe` possui a flag `ativo` (`boolean`). Cafés vinculados a históricos ou desativados não quebram a integridade referencial de pedidos antigos.

### 2.3. Herança JPA `JOINED` Dupla (`Usuario` e `Pagamento`)
O modelo relacional utiliza a estratégia `@Inheritance(strategy = InheritanceType.JOINED)` em dois pontos críticos:
1. **Hierarquia de Usuários:**
   - Tabela pai `usuario` armazena credenciais, CPF, data de nascimento e perfis (`@ElementCollection`).
   - Tabelas filhas `administrador` (com `cargo`) e `cliente` compartilham a mesma chave primária da tabela `usuario`.
2. **Hierarquia de Pagamentos:**
   - Tabela pai `pagamento` armazena `valor`, `confirmado`, `dataConfirmacao` e vínculo `id_pedido`.
   - Tabelas filhas especializadas:
     - `pagamentopix` (`chavePix` UUID gerada automaticamente, `dataExpiracao` de 30 minutos).
     - `pagamentocartao` (`nomeTitular`, `numeroCartao` **mascarado automaticamente** no formato `**** **** **** 1234` antes de persistir, `bandeira`).
     - `pagamentoboleto` (`codigoBarras` gerado automaticamente, `dataVencimento` para +3 dias úteis).

### 2.4. Serialização Híbrida de Enums (`@JsonFormat(Shape.OBJECT)` + `@JsonCreator`)
Todos os enums de domínio (`NivelDeTorra`, `Tratamento`, `NotaSensorial`, `PedidoStatus`, `Perfil`, `Regiao`) são anotados com `@JsonFormat(shape = JsonFormat.Shape.OBJECT)`.
- **Nas respostas JSON (`GET`):** os enums não retornam apenas uma string, mas sim um **objeto rico** contendo metadados prontos para exibição em UI, por exemplo:
  ```json
  "nivelDeTorra": { "id": 2, "nome": "Torra Média" },
  "tratamento": { "id": 1, "nome": "Natural", "descricao": "Secagem do fruto inteiro ao sol, ressaltando doçura e corpo." }
  ```
- **No envio de `notasSensoriais` em `CafeDTO` (`POST/PUT /cafes`):** o enum `NotaSensorial` possui um `@JsonCreator(mode = JsonCreator.Mode.DELEGATING)` customizado (`fromObject`) que aceita tanto strings com o nome do enum (`"CHOCOLATE"`, `"FLORAL"`) quanto objetos contendo o ID (`{ "id": 5 }`), oferecendo flexibilidade total ao cliente HTTP.

### 2.5. Regras Automáticas de Frete e Snapshot de Preço no Pedido
Ao criar um pedido (`POST /pedidos`), o cliente envia **apenas** o `idEnderecoEntrega` e a lista de `itens` (`[{ idCafe, quantidade }]`). O servidor executa:
- **Snapshot de Preço:** O `precoUnitario` de cada `ItemPedido` é capturado diretamente do cadastro atual do `Cafe` no banco (ignorando qualquer tentativa de manipulação de preço pelo cliente).
- **Cálculo Automático de Frete:**
  - Compras com subtotal **inferior a R$ 180,00**: acréscimo automático de frete fixo de **R$ 15,00**.
  - Compras com subtotal **igual ou superior a R$ 180,00**: **Frete Grátis (`R$ 0,00`)**.
- **Máquina de Estados e Estorno de Estoque:** O pedido nasce como `AGUARDANDO_PAGAMENTO (1)`, avança para `PAGO (2)` após processamento em `/pagamentos/*`, e só pode ser cancelado (`POST /pedidos/{id}/cancelamento`) se estiver em `AGUARDANDO_PAGAMENTO` ou `PAGO`. O cancelamento **devolve automaticamente** as quantidades de cada item ao `Estoque`.

---

## 3. Modelo de Domínio e Enums Especializados

### Enums e Códigos Aceitos pela API

| Enum | Valores (`id` — `label/nome`) | Persistência no Banco |
| :--- | :--- | :--- |
| **`NivelDeTorra`** | `1`: Clara, `2`: Média-Clara, `3`: Média, `4`: Média-Escura, `5`: Escura | `@Enumerated(EnumType.STRING)` |
| **`Tratamento`** | `1`: Natural (Seco), `2`: Lavado (Wet Process), `3`: Honey, `4`: Semi-Lavado, `5`: Descascado Natural, `6`: Anaeróbico, `7`: Fermentação Controlada, `8`: Experimental / Híbrido | `@Enumerated(EnumType.STRING)` |
| **`NotaSensorial`** | `1`: Chocolate Amargo, `2`: Chocolate ao Leite, `3`: Caramelo, `4`: Nozes, `5`: Amêndoas, `6`: Avelã, `7`: Frutas Cítricas, `8`: Frutas Vermelhas, `9`: Frutas Amarelas, `10`: Frutas Tropicais, `11`: Pêssego, `12`: Maçã Verde, `13`: Floral, `14`: Jasmim, `15`: Mel, `16`: Melaço, `17`: Especiarias, `18`: Baunilha, `19`: Vinho / Fermentado, `20`: Herbal | `@ElementCollection` (`STRING`) |
| **`PedidoStatus`** | `1`: Aguardando Pagamento, `2`: Pagamento Confirmado (`PAGO`), `3`: Em Separação, `4`: Enviado, `5`: Entregue, `6`: Cancelado | `@Enumerated(EnumType.STRING)` |
| **`Perfil`** | `1`: `Administrador`, `2`: `Usuario` | `@ElementCollection` (`id_perfil` via `PerfilConverter`) |
| **`Regiao`** | `1`: Centro-Oeste, `2`: Nordeste, `3`: Norte, `4`: Sudeste, `5`: Sul | `Integer` (via `RegiaoConverter`) |

---

## 4. Segurança, Autenticação JWT e Controle de Propriedade (RBAC + Ownership)

1. **Criptografia de Senhas (`HashServiceImpl`):**
   - As senhas nunca são salvas em texto puro; utiliza-se `PBKDF2WithHmacSHA512` com salt fixo de aplicação, gerando hash Base64 de 512 bits.
2. **Assinatura Assimétrica RSA-2048 (`JwtServiceImpl`):**
   - No login (`POST /auth/login`), o token JWT é assinado com a chave privada `classpath:token/privateKey.pem` e validado pelo Quarkus via `classpath:token/publicKey.pem` com `issuer = "unitins-jwt"` e validade de **24 horas**.
   - O token é retornado simultaneamente no **corpo da resposta** (`AuthDTOResponse.token`) e no cabeçalho HTTP `Authorization: Bearer <token>`.
3. **Validação de Dono do Recurso (`SecurityService`):**
   - Além do controle por papéis (`@RolesAllowed({"Administrador", "Usuario"})`), o serviço `SecurityService` inspeciona o `JsonWebToken` injetado para garantir que um cliente com perfil `Usuario` **só possa visualizar, alterar ou excluir seus próprios endereços, telefones, pedidos e pagamentos**, enquanto o perfil `Administrador` possui visibilidade global.

---

## 5. Padronização de Erros (RFC 7807 — Problem Details)

Todas as exceções de validação, negócio, banco de dados e segurança são interceptadas por *ExceptionMappers* globais (`ValidationExceptionMapper`, `BusinessExceptionMapper`, `NotFoundExceptionMapper`, `ConstraintViolationExceptionMapper`, `UncaughtExceptionMapper`) e formatadas segundo a especificação **RFC 7807 (`application/problem+json`)**:

```json
{
  "type": "http://localhost:8080/errors/business-rule",
  "title": "Violação de Regra de Negócio",
  "status": 422,
  "detail": "Não é possível excluir a marca pois existem cafés vinculados a ela.",
  "instance": "/marcas/1",
  "timestamp": "2026-11-05T18:00:00Z"
}
```

Quando ocorrem erros de validação de campos (`@Valid` — HTTP `400 Bad Request`), o objeto `Problem` inclui a lista `errors` detalhando cada campo (`field` e `message`).

---

## 6. Catálogo Completo de Endpoints da API

### 6.1. Autenticação (`/auth`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | `@PermitAll` | Autentica usuário por `login` e `senha`, retornando `AuthDTOResponse` e JWT. |

### 6.2. Catálogo de Cafés (`/cafes`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/cafes` | `@PermitAll` | Lista cafés ativos. Aceita query params opcionais `?nome=...` ou `?minPontuacao=84&maxPontuacao=90`. |
| `GET` | `/cafes/{id}` | `@PermitAll` | Retorna a ficha sensorial e de estoque detalhada de um café pelo ID. |
| `POST` | `/cafes` | `Administrador` | Cadastra um novo café e inicializa automaticamente sua entidade `Estoque`. |
| `PUT` | `/cafes/{id}` | `Administrador` | Atualiza dados do café e sincroniza o saldo na entidade `Estoque`. |
| `DELETE` | `/cafes/{id}` | `Administrador` | Remove um café do catálogo (ou valida integridade referencial). |

### 6.3. Marcas (`/marcas`) e Categorias do Café (`/categorias`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/marcas` | `@PermitAll` | Lista todas as marcas/torrefações (suporta `?nome=...`). |
| `GET` | `/marcas/{id}` | `@PermitAll` | Busca marca por ID. |
| `POST` | `/marcas` | `Administrador` | Cria nova marca (`nome` único). |
| `PUT` | `/marcas/{id}` | `Administrador` | Atualiza marca existente. |
| `DELETE` | `/marcas/{id}` | `Administrador` | Exclui marca (bloqueado caso existam cafés associados). |
| `GET` | `/categorias` | `@PermitAll` | Lista todas as categorias de café (suporta `?nome=...`). |
| `GET` | `/categorias/{id}` | `@PermitAll` | Busca categoria por ID. |
| `POST` | `/categorias` | `Administrador` | Cria nova categoria de café. |
| `PUT` | `/categorias/{id}` | `Administrador` | Atualiza categoria existente. |
| `DELETE` | `/categorias/{id}` | `Administrador` | Exclui categoria (bloqueado caso existam cafés associados). |

### 6.4. Clientes (`/clientes`) e Usuários (`/usuarios`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/clientes` | `@PermitAll` | Auto-cadastro público de cliente com CPF, telefones e endereços iniciais. |
| `GET` | `/clientes/{id}` | `Administrador`, `Usuario` | Busca dados de um cliente (valida propriedade se for `Usuario`). |
| `PUT` | `/clientes/{id}` | `Administrador`, `Usuario` | Atualiza dados cadastrais do cliente. |
| `GET` | `/usuarios/me` | `Administrador`, `Usuario` | Retorna o perfil completo (com telefones e endereços) do usuário logado no token JWT. |
| `PATCH` | `/usuarios/me/senha` | `Administrador`, `Usuario` | Altera a própria senha validando `senhaAntiga` e `novaSenha`. |
| `GET` | `/usuarios` | `Administrador` | Lista todos os usuários do sistema (suporta `?nome=...`). |
| `GET` | `/usuarios/{id}` | `Administrador` | Busca usuário por ID. |
| `POST` | `/usuarios` | `Administrador` | Cria usuário definindo perfis administrativos/cliente. |
| `PUT` | `/usuarios/{id}` | `Administrador` | Atualiza usuário pelo ID. |
| `DELETE` | `/usuarios/{id}` | `Administrador` | Remove usuário do sistema. |

### 6.5. Endereços (`/enderecos`) e Telefones (`/telefones`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/enderecos` | `Administrador` | Lista todos os endereços cadastrados. |
| `GET` | `/enderecos/{id}` | `Administrador`, `Usuario` | Busca endereço por ID (valida posse do endereço). |
| `POST` | `/enderecos` | `Administrador`, `Usuario` | Cadastra novo endereço vinculado ao usuário autenticado. |
| `PUT` | `/enderecos/{id}` | `Administrador`, `Usuario` | Atualiza endereço existente do usuário. |
| `DELETE` | `/enderecos/{id}` | `Administrador`, `Usuario` | Remove endereço (bloqueado se vinculado a pedido). |
| `GET` | `/telefones/usuario/{idUsuario}` | `Administrador`, `Usuario` | Lista telefones de um usuário. |
| `POST` | `/telefones` | `Administrador`, `Usuario` | Adiciona novo telefone (`ddd` + `numero`) ao usuário logado. |
| `PUT` | `/telefones/{id}` | `Administrador`, `Usuario` | Atualiza telefone existente. |
| `DELETE` | `/telefones/{id}` | `Administrador`, `Usuario` | Remove telefone do usuário. |

### 6.6. Pedidos (`/pedidos`) e Pagamentos (`/pagamentos`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/pedidos` | `Usuario` | Cria pedido, valida posse do endereço, baixa estoque e calcula frete. |
| `GET` | `/pedidos/me` | `Usuario` | Lista todo o histórico de pedidos do cliente autenticado. |
| `GET` | `/pedidos` | `Administrador` | Lista todos os pedidos da loja (visão administrativa). |
| `GET` | `/pedidos/{id}` | `Administrador`, `Usuario` | Consulta detalhes de um pedido específico. |
| `POST` | `/pedidos/{id}/cancelamento` | `Administrador`, `Usuario` | Cancela o pedido (se `AGUARDANDO_PAGAMENTO` ou `PAGO`) e estorna o estoque. |
| `POST` | `/pagamentos/pix` | `Usuario` | Gera e confirma pagamento via PIX (`chavePix`, `dataExpiracao`). |
| `POST` | `/pagamentos/cartao` | `Usuario` | Processa pagamento via Cartão de Crédito (mascara número do cartão). |
| `POST` | `/pagamentos/boleto` | `Usuario` | Gera pagamento via Boleto Bancário (`codigoBarras`, `dataVencimento`). |

### 6.7. Localidades (`/estados` e `/municipios`)
| Método | Rota | Permissão | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/estados` | `@PermitAll` | Lista todos os 27 estados brasileiros e suas macrorregiões (`?nome=...`). |
| `POST/PUT/DELETE` | `/estados` | `Administrador` | CRUD administrativo de estados. |
| `GET` | `/municipios` | `@PermitAll` | Lista todos os municípios com seus respectivos estados (`?nome=...`). |
| `POST/PUT/DELETE` | `/municipios` | `Administrador` | CRUD administrativo de municípios. |

---

## 7. Requisitos e Configuração do Ambiente

- **Java JDK:** 21
- **Banco de Dados:** PostgreSQL 15+ rodando na porta `5432`
- **Node.js & npm:** Node 20+ (para o frontend Angular 21)

### Configuração do PostgreSQL (`application.properties`)
Crie o banco e usuário conforme configurado em `dorana/backend/src/main/resources/application.properties`:
```sql
CREATE USER topicos1 WITH PASSWORD '123456';
CREATE DATABASE topicos1db OWNER topicos1;
GRANT ALL PRIVILEGES ON DATABASE topicos1db TO topicos1;
```
> Ao iniciar o Quarkus em modo `dev` ou `test`, o Hibernate recria automaticamente o esquema (`drop-and-create`) e executa a carga inicial do arquivo `import.sql`.

---

## 8. Como Executar (Backend Quarkus, Frontend Angular e Testes)

### 8.1. Executar o Backend (Quarkus)
A partir do diretório `dorana/`:
```bash
cd dorana
./mvnw quarkus:dev
```
- **API Base URL:** `http://localhost:8080`
- **Swagger UI (OpenAPI Interativo):** `http://localhost:8080/q/swagger-ui`
- **Dev UI do Quarkus:** `http://localhost:8080/q/dev-ui`

### 8.2. Executar a Suíte de Testes Automatizados do Backend
O projeto conta com **13 classes de testes de integração (65 testes REST Assured)** cobrindo autenticação, RBAC, catálogo, estoque, pedidos, pagamentos e validações:
```bash
cd dorana
./mvnw test
```

### 8.3. Executar o Frontend (Angular 21 — Dorana Coffee Shop)
Em outro terminal, acesse `dorana/frontend`:
```bash
cd dorana/frontend
npm install
npm start
```
- **Loja Virtual & Painel Administrativo:** `http://localhost:4200`
- **Build de Produção / SSR:** `npm run build`
- **Testes Unitários (Vitest):** `npx ng test --watch=false`

---

## 9. Credenciais Padrão de Desenvolvimento (Seed Data)

O arquivo `import.sql` popula automaticamente duas contas prontas para testes imediatos:

| Perfil | Login | Senha | Permissões Principais |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin` | `123456` | Gestão completa de cafés, estoque, marcas, categorias, estados, municípios, usuários e visualização global de pedidos. |
| **Cliente (`Usuario`)** | `cliente` | `123456` | Compra de cafés, gestão de múltiplos endereços/telefones, fechamento de pedidos (`POST /pedidos`), pagamentos PIX/Cartão/Boleto e histórico em `/pedidos/me`. |

---

## 10. Exemplos Práticos de Requisições (cURL)

### 10.1. Autenticar e Obter Token JWT
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login": "cliente", "senha": "123456"}'
```

### 10.2. Consultar Catálogo de Cafés por Faixa de Pontuação SCA
```bash
curl -X GET "http://localhost:8080/cafes?minPontuacao=85&maxPontuacao=92"
```

### 10.3. Criar um Pedido (`POST /pedidos`)
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Authorization: Bearer <SEU_TOKEN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "idEnderecoEntrega": 1,
    "itens": [
      { "idCafe": 1, "quantidade": 2 }
    ]
  }'
```

### 10.4. Pagar o Pedido via PIX (`POST /pagamentos/pix`)
```bash
curl -X POST http://localhost:8080/pagamentos/pix \
  -H "Authorization: Bearer <SEU_TOKEN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{ "idPedido": 2 }'
```

---

## Licença e Autoria

Projeto acadêmico e técnico desenvolvido na disciplina de **Tópicos em Programação I (Unitins)**, evoluído com a identidade visual e experiência Full Stack **Dorana Coffee Shop**.
