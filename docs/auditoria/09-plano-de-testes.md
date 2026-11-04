# 09 - Plano de Testes Automatizados e Matriz de Cenários

**Data:** 21/10/2026  
**Arquiteto de Testes:** Antigravity  
**Fase:** 6 (Rede de Testes Automatizados)  
**Stack de Teste:** `@QuarkusTest` + REST Assured + Hamcrest + JUnit 5 + Mockito + PostgreSQL real  

---

## 1. Princípios de Isolamento e Arquitetura de Testes

1. **Testes Baseados no Comportamento Desejado:** Os novos testes são escritos estritamente a partir das regras de negócio e contratos REST aprovados, e **nunca** a partir do comportamento legado defeituoso.
2. **Independência Total e Sem Ordem Fixa:** Proibido o uso de `@TestMethodOrder(OrderAnnotation.class)` ou variáveis de classe estáticas compartilhadas (`static Long id`). Cada método de teste executa no padrão Arrange-Act-Assert (AAA), criando suas próprias entidades e dependências via **Test Data Builders**.
3. **Assertividade de Contrato REST:**
   - Todo teste de criação (`POST`) deve verificar `HTTP 201 Created` e a presença do header `Location`.
   - Todo teste de erro deve verificar o formato JSON RFC 7807 Problem Details (`application/problem+json`).
   - Todo teste de recurso inexistente deve verificar `HTTP 404 Not Found` com corpo estruturado.
   - Todo teste de segurança deve validar `401 Unauthorized` (sem token) e `403 Forbidden` (token com role inadequada ou tentando acessar dados de terceiros - BOLA/IDOR).

---

## 2. Matriz de Cobertura de Endpoints × Cenários

| Endpoint | Caminho Feliz (200/201/204) | Entrada Inválida (400/422) | Não Encontrado (404) | Conflito / Duplicidade (409) | Sem Token (401) | Papel Errado / IDOR (403) | Location Header |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| `POST /auth/login` | 200 (Token no header e corpo) | 400 (corpo nulo/em branco) | - | - | 401 (senha ou e-mail errados) | - | N/A |
| `POST /clientes` | 201 (Autocadastro público) | 400 (CPF/e-mail inválidos) | - | 409 (CPF ou e-mail duplicado) | N/A (PermitAll) | N/A | Sim |
| `GET /clientes` | 200 (Lista todos os clientes) | - | - | - | 401 | 403 (Cliente comum bloqueado) | N/A |
| `GET /clientes/{id}` | 200 (Dono ou Admin) | - | 404 | - | 401 | 403 (Cliente acessando outro ID) | N/A |
| `PUT /clientes/{id}` | 200 (Dono ou Admin) | 400 (campos inválidos) | 404 | 409 (e-mail duplicado) | 401 | 403 (Cliente alterando outro ID) | N/A |
| `DELETE /clientes/{id}` | 204 (Dono ou Admin) | - | 404 | 409 (Cliente com pedidos) | 401 | 403 (Cliente excluindo outro) | N/A |
| `GET /usuarios/me` | 200 (Perfil do usuário logado) | - | - | - | 401 | - | N/A |
| `PUT /usuarios/me/senha` | 204 (Senha alterada com sucesso) | 400 (senha fraca / em branco) | - | - | 401 | - | N/A |
| `POST /administradores` | 201 (Novo Admin criado) | 400 (dados inválidos) | - | 409 (e-mail duplicado) | 401 | 403 (Cliente comum bloqueado) | Sim |
| `GET /cafes` | 200 (Catálogo com estoque) | 400 (filtros inválidos) | - | - | N/A (PermitAll) | N/A | N/A |
| `GET /cafes/{id}` | 200 (Detalhes completos) | - | 404 | - | N/A (PermitAll) | N/A | N/A |
| `POST /cafes` | 201 (Criado por Admin) | 400 (preço negativo/nulo) | 404 (marca inexistente) | - | 401 | 403 (Cliente comum bloqueado) | Sim |
| `PUT /cafes/{id}` | 200 (Atualizado por Admin) | 400 (dados inválidos) | 404 | - | 401 | 403 | N/A |
| `DELETE /cafes/{id}` | 204 (Soft delete / inativação) | - | 404 | - | 401 | 403 | N/A |
| `GET /categorias` | 200 (Público) | - | - | - | N/A | N/A | N/A |
| `POST /categorias` | 201 (Admin) | 400 | - | 409 (nome duplicado) | 401 | 403 | Sim |
| `GET /marcas` | 200 (Público) | - | - | - | N/A | N/A | N/A |
| `POST /marcas` | 201 (Admin) | 400 | - | 409 (nome duplicado) | 401 | 403 | Sim |
| `GET /regioes-origem` | 200 (Público) | - | - | - | N/A | N/A | N/A |
| `POST /regioes-origem` | 201 (Admin) | 400 | - | - | 401 | 403 | Sim |
| `POST /pedidos` | 201 (Estoque reservado) | 400 (sem itens/itens nulos) | 404 (café/endereço inexistente) | 409 (Estoque insuficiente) | 401 | 403 (endereço de outro usuário) | Sim |
| `GET /pedidos/{id}` | 200 (Dono ou Admin) | - | 404 | - | 401 | 403 (Tentativa de ver pedido alheio) | N/A |
| `POST /pedidos/{id}/cancelamento` | 200 (Status CANCELADO, estoque estornado) | 400 (pedido já entregue) | 404 | - | 401 | 403 (Cliente cancelando pedido alheio) | N/A |
| `POST /pagamentos/cartao` | 201 (Pedido vira PAGO) | 400 (cvv recusado / dados inválidos) | 404 (pedido inexistente) | 409 (pedido já pago) | 401 | 403 (pagar pedido de outro usuário) | Sim |
| `POST /pagamentos/pix` | 201 (Gera chave e expiração) | 400 | 404 | 409 (já pago) | 401 | 403 | Sim |
| `POST /pagamentos/boleto` | 201 (Gera linha digitável) | 400 | 404 | 409 (já pago) | 401 | 403 | Sim |
| `GET /enderecos` | 200 (Admin ou Dono) | - | - | - | 401 | 403 | N/A |
| `POST /enderecos` | 201 (Para o usuário do token) | 400 | 404 (município inexistente) | - | 401 | - | Sim |
| `GET /estados` | 200 (Público) | - | - | - | N/A | N/A | N/A |
| `GET /municipios` | 200 (Público) | - | - | - | N/A | N/A | N/A |

---

## 3. Testes Dedicados para Regras Críticas de Negócio

1. **Teste de Concorrência e Estoque Insuficiente:**
   - Cenário: Café com estoque de 5 unidades. Duas threads paralelas solicitam 4 unidades cada.
   - Resultado Esperado: Uma transação é aceita com `201`; a segunda falha com `HTTP 409 Conflict` (ou `HTTP 400` com mensagem clara de estoque insuficiente), sem saldo negativo.
2. **Teste de Snapshot de Preço:**
   - Cenário: Cria café com preço R$ 50,00. Cliente cria pedido. Em seguida, Admin atualiza preço do café para R$ 80,00.
   - Resultado Esperado: Consulta ao pedido realizado mantém valor de R$ 50,00 no `ItemPedido` e total inalterado.
3. **Teste de Cancelamento e Estorno de Estoque:**
   - Cenário: Café com saldo 100. Pedido compra 10 unidades (saldo vai para 90). Chamada a `POST /pedidos/{id}/cancelamento`.
   - Resultado Esperado: Status do pedido atualiza para `CANCELADO`; consulta subsequente ao café mostra estoque restaurado em 100 unidades.
4. **Teste de Bloqueio de Escalada de Privilégios:**
   - Cenário: Cliente comum envia payload tentando passar `perfil: "ADMINISTRADOR"` ou alterar privilégios.
   - Resultado Esperado: O sistema ignora qualquer tentativa ou rejeita a requisição, mantendo o perfil derivado do tipo da entidade.
