# 01 - Linha de Base da Aplicação (Baseline)

**Data:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 0 (Reconhecimento e Linha de Base - Somente Leitura)  

---

## 1. Estado do Repositório Git

- **Branch Atual:** `main`
- **Estado de Versionamento:** Existem arquivos modificados e não commitados na branch de trabalho decorrentes das etapas anteriores de auditoria, testes e implementação do front-end.
- **Configuração de Capitalização:** `core.ignorecase = true` (ambiente Windows). Atenção crítica: qualquer renomeação de capitalização de pastas (ex.: `Usuario` → `usuario`) deverá ser executada em **duas etapas** via diretório temporário intermediário (`git mv Pasta pasta_tmp && git mv pasta_tmp pasta`) para garantir que o Git e o sistema de arquivos registrem a mudança sem corrupção.

---

## 2. Linha de Base do Backend (Java / Quarkus / Maven)

- **Build Tool:** Apache Maven Wrapper (`mvnw.cmd`).
- **GroupId:** `br.unitins.topicos1.lgc`
- **Artefato:** `lgc-api-tp1` (versão `1.0.0-SNAPSHOT`).
- **Java / Framework:** Java 21, Quarkus 3.26.3, Hibernate ORM 7.1.0, PostgreSQL JDBC.
- **Status da Compilação:** `BUILD SUCCESS` (0 erros).
- **Status dos Testes:** `BUILD SUCCESS` (65 testes executados, 0 falhas, 0 erros, 0 ignorados).
- **Lista de Endpoints Expostos na Linha de Base:**
  - `POST /auth/login`
  - `GET /cafes`, `GET /cafes/{id}`, `POST /cafes`, `PUT /cafes/{id}`, `DELETE /cafes/{id}`
  - `GET /categorias`, `GET /categorias/{id}`, `POST /categorias`, `PUT /categorias/{id}`, `DELETE /categorias/{id}`
  - `GET /marcas`, `GET /marcas/{id}`, `POST /marcas`, `PUT /marcas/{id}`, `DELETE /marcas/{id}`
  - `GET /estados`, `GET /estados/{id}`, `POST /estados`, `PUT /estados/{id}`, `DELETE /estados/{id}`
  - `GET /municipios`, `GET /municipios/{id}`, `POST /municipios`, `PUT /municipios/{id}`, `DELETE /municipios/{id}`
  - `POST /clientes`, `GET /clientes/{id}`, `GET /clientes`, `DELETE /clientes/{id}`
  - `GET /enderecos`, `GET /enderecos/{id}`, `POST /enderecos`, `PUT /enderecos/{id}`, `DELETE /enderecos/{id}`
  - `GET /telefones`, `GET /telefones/{id}`, `POST /telefones`, `PUT /telefones/{id}`, `DELETE /telefones/{id}`
  - `GET /usuarios`, `GET /usuarios/me`, `GET /usuarios/{id}`, `POST /usuarios`, `PUT /usuarios/{id}`, `PUT /usuarios/me/senha`, `DELETE /usuarios/{id}`
  - `GET /pedidos`, `GET /pedidos/meus-pedidos`, `GET /pedidos/{id}`, `POST /pedidos`, `PATCH /pedidos/{id}/cancelar`
  - `POST /pagamentos/cartao`, `POST /pagamentos/boleto`, `POST /pagamentos/pix`
  - `GET /estoque/{idCafe}`
- **Schema do Banco de Dados:** Mapeado via JPA (tabelas `administrador`, `usuario`, `cliente`, `cafe`, `estoque`, `pedido`, `itempedido`, `pagamento`, `pagamentocartao`, `pagamentoboleto`, `pagamentopix`, `telefone`, `endereco`, `municipio`, `estado`, `marca`, `categoriadocafe`, `usuario_perfil`, `cafe_notasensorial`).

---

## 3. Linha de Base do Frontend (Angular / TypeScript)

- **Framework:** Angular 21 Standalone (`@angular/core`: `^21.1.0`), Angular Material 21 (`^21.2.14`).
- **TypeScript:** `~5.9.2`.
- **Status do Build:** `npm run build` executado com **SUCESSO** (`Exit code 0`).
- **Status dos Testes:** `npx ng test --watch=false` executado com **SUCESSO** (4 arquivos, 9 testes, 0 falhas).
