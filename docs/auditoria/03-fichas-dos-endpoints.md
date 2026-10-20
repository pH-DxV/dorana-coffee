# 03 - Fichas de Rastreamento de Endpoints (Funcionamento Atual)

**Data da Auditoria:** 20/10/2026  
**Auditor:** Rastreador de Fluxos & Auditor REST  
**Fase:** 2 (Rastreamento de Ponta a Ponta)  
**Ambiente:** Quarkus Dev Mode (JVM 21, PostgreSQL 17 Local)  

---

## Índice de Recursos Auditados
1. [AuthResource (`/auth`)](#1-authresource-auth)
2. [CafeResource (`/cafes`)](#2-caferesource-cafes)
3. [CategoriaDoCafeResource (`/categorias`)](#3-categoriadocaferesource-categorias)
4. [ClienteResource (`/clientes`)](#4-clienteresource-clientes)
5. [EnderecoResource (`/endereco`)](#5-enderecoresource-endereco)
6. [EstadoResource (`/estados`)](#6-estadoresource-estados)
7. [MarcaResource (`/marcas`)](#7-marcaresource-marcas)
8. [MunicipioResource (`/municipios`)](#8-municipioresource-municipios)
9. [PagamentoResource (`/pagamentos`)](#9-pagamentoresource-pagamentos)
10. [PedidoResource (`/pedidos`)](#10-pedidoresource-pedidos)
11. [RegiaoResource (`/regioes`)](#11-regiaoresource-regioes)
12. [TelefoneResource (`/telefones`)](#12-telefoneresource-telefones)
13. [UsuarioResource (`/usuarios`)](#13-usuarioresource-usuarios)

---

## 1. AuthResource (`/auth`)

### Endpoint 1.1: `POST /auth/login`
- **Contrato:**
  - **Verbo / Rota:** `POST /auth/login`
  - **Segurança:** `@PermitAll` (público)
  - **Content-Types:** Consumes: `application/json`, Produces: `application/json`
  - **Corpo Esperado (`AuthDTO`):** `{"login": "string", "senha": "string"}` (`@NotBlank` em ambos)
- **Fluxo Interno Completo:**
  1. `AuthResource.login(@Valid AuthDTO dto)`
  2. Chamada a `hashService.getHashSenha(dto.senha())`: Converte a senha recebida em hash SHA-256 codificado em Base64 com salt padrão.
  3. Chamada a `usuarioService.findByLoginAndSenha(dto.login(), hashDaSenha)`.
  4. `UsuarioRepository.findByLoginAndSenha`: Executa HQL `login = ?1 AND senha = ?2` na tabela `usuario`.
  5. Se o retorno for nulo: o método retorna imediatamente `Response.status(Status.NO_CONTENT).build()`.
  6. Se o usuário for encontrado: chamada a `jwtService.generateJwt(usuario)` que monta as claims JWT (`sub`, `upn`, `groups`, `id`) e assina com a chave RSA privada.
  7. Retorno: `Response.ok().header("Authorization", token).build()`.
  - **Transação:** Não anotado com `@Transactional` (leitura pura).
- **Regras e Validações:**
  - `@Valid` no Resource valida presença de login e senha.
  - Verificação de hash da senha.
- **Comportamento Observado na Prática:**
  - **Caminho Feliz (login correto):** Retorna `HTTP 200 OK`, header `Authorization: eyJ...`, corpo **vazio** (`content-length: 0`).
  - **Senha incorreta ou usuário inexistente:** Retorna **`HTTP 204 No Content`**!
- **Divergências Encontradas:**
  - **Violação REST Crítica:** Retornar `204 No Content` para falha de autenticação é gravíssimo. `204` indica sucesso. O correto obrigatório pelo protocolo HTTP é `401 Unauthorized`.
  - **Corpo Vazio no Sucesso:** O token é injetado apenas no header `Authorization` e o corpo é vazio, impedindo o cliente de receber dados do usuário logado (nome, perfis, expiração) sem fazer um parse manual do token JWT no cliente Angular.

---

## 2. CafeResource (`/cafes`)

### Endpoint 2.1: `GET /cafes`
- **Contrato:** `GET /cafes`, `@PermitAll`, Produces: `application/json`. Sem parâmetros de paginação.
- **Fluxo Interno:**
  1. `CafeResource.findAll()` -> `CafeServiceImpl.findAll()`
  2. `CafeRepository.listAll()` -> `SELECT * FROM cafe`
  3. Para CADA café da lista, executa `converterParaDTO(cafe)` que chama `estoqueService.consultarQuantidade(cafe.getId())` -> `SELECT * FROM estoque WHERE id_cafe = ?`
  4. Carrega `cafe_notasensorial` e relações `marca` e `categoriaDoCafe`.
- **Comportamento Observado:**
  - Retorna `HTTP 200 OK` com array JSON de cafés.
  - **SQL Executado:** Dispara 1 query para cafés e N queries adicionais para a tabela `estoque`.
- **Divergências:**
  - **N+1 Queries Confirmado.**
  - **Ausência de Paginação:** Retorna a tabela inteira na memória.

### Endpoint 2.2: `GET /cafes/{id}`
- **Contrato:** `GET /cafes/{id}`, `@PermitAll`.
- **Fluxo Interno:** `CafeResource.findById(id)` -> `CafeServiceImpl.findById(id)` -> `CafeRepository.findById(id)`. Se null, lança `NotFoundException("Café não encontrado.")`.
- **Comportamento Observado:**
  - ID existente: `200 OK` com DTO completo.
  - ID inexistente (ex.: 9999): Retorna `HTTP 404 Not Found` com corpo vazio (sem RFC 7807).

### Endpoint 2.3: `GET /cafes/search/nome/{nome}`
- **Contrato:** `GET /cafes/search/nome/{nome}`, `@PermitAll`.
- **Fluxo Interno:** `CafeRepository.findByNomeLike`: `UPPER(nome) LIKE %...%`.
- **Comportamento Observado:** `200 OK` com lista.
- **Divergências:** Rota não segue padrão REST. O padrão canônico para filtros em coleções é query parameter: `GET /cafes?nome=...` em vez de criar um segmento com verbo `/search/nome/{nome}`.

### Endpoint 2.4: `GET /cafes/search/pontuacao?min={min}&max={max}`
- **Contrato:** `GET /cafes/search/pontuacao`, `@QueryParam("min")`, `@QueryParam("max")`.
- **Fluxo Interno:** Validação manual no Resource: `if (minSCA == null && maxSCA == null) return Response.status(400).entity("Informe ao menos um valor...").build();`
- **Comportamento Observado:**
  - Com parâmetros: `200 OK`.
  - Sem parâmetros: `HTTP 400 Bad Request` retornando texto puro em vez de JSON Problem Details.
- **Divergências:** Lógica de validação embutida diretamente no Resource; rota com verbo `/search/pontuacao` que deveria ser query params no endpoint raiz `GET /cafes?minPontuacao=...&maxPontuacao=...`.

### Endpoint 2.5: `POST /cafes`
- **Contrato:** `POST /cafes`. Segurança: `// @RolesAllowed("Administrador")` (COMENTADO).
- **Fluxo Interno:**
  1. `CafeServiceImpl.create`: Persiste a entidade `Cafe` com base no `CafeDTO`.
  2. Chama `estoqueService.iniciarEstoque(cafe.getId(), dto.estoque())` que insere registro na tabela `estoque`.
- **Comportamento Observado:**
  - Retorna `HTTP 201 Created` sem header `Location`.
  - **Falha de Segurança Confirmada:** Qualquer cliente não autenticado consegue cadastrar produtos.

### Endpoint 2.6: `PUT /cafes/{id}`
- **Contrato:** `PUT /cafes/{id}`. Segurança comentada (aberta). `@Transactional` no Resource e no Service.
- **Fluxo Interno:** Atualiza atributos do café e chama `estoqueService.atualizarEstoque(id, dto.estoque())`.
- **Comportamento Observado:** `HTTP 200 OK`. Sem validação de concorrência.

### Endpoint 2.7: `DELETE /cafes/{id}`
- **Contrato:** `DELETE /cafes/{id}`. Segurança comentada (aberta).
- **Fluxo Interno:** `estoqueService.excluirEstoque(id)` seguido de `cafeRepository.delete(cafe)`.
- **Comportamento Observado:**
  - Se o café possui histórico em `itempedido`: Retorna **`HTTP 500 Internal Server Error`** com `PSQLException: foreign key violation` no console.
  - Deveria haver soft-delete ou validação impedindo a exclusão física.

---

## 3. CategoriaDoCafeResource (`/categorias`)

### Endpoints:
- `GET /categorias`: Lista todas. Segurança comentada. Retorna `200 OK`.
- `GET /categorias/{id}`: Busca por ID. Retorna `200 OK` ou `404 Not Found`.
- `GET /categorias/search/{nome}`: Busca por nome via path param. Retorna `200 OK`.
- `POST /categorias`: Criação. `@RolesAllowed` comentado! Retorna `201 Created` sem `Location`.
- `PUT /categorias/{id}`: Atualização. `@RolesAllowed` comentado. Retorna `200 OK`.
- `DELETE /categorias/{id}`: Exclusão. `@RolesAllowed` comentado. Retorna `204 No Content`.
- **Divergências Globais:**
  - O Resource se chama `CategoriaDoCafeResource`, mas a rota é `/categorias`.
  - Todas as anotações `@RolesAllowed({"Administrador"})` estão desativadas com `//`. Qualquer pessoa altera e apaga categorias.

---

## 4. ClienteResource (`/clientes`)

### Endpoint 4.1: `POST /clientes`
- **Contrato:** `POST /clientes`, `@PermitAll`, `@Valid ClienteDTO dto`.
- **Fluxo Interno:**
  1. Valida unicidade de CPF e Login via Repository. Lança `ValidationException` se duplicado.
  2. Força perfil fixo `Perfil.USER` (`entity.setPerfis(Set.of(Perfil.USER))`).
  3. Processa telefones e endereços em lista criando instâncias associadas.
  4. `repository.persist(entity)`.
- **Comportamento Observado:** `201 Created` sem header `Location`.

### Endpoint 4.2: `PUT /clientes/{id}`
- **Contrato:** `PUT /clientes/{id}`, `@RolesAllowed({"Administrador", "Usuario"})`.
- **Fluxo Interno:** Executa `validarPermissao(id)`. Se for cliente comum e o ID da URL for diferente do ID do token JWT, lança `ForbiddenException`.
- **Comportamento Observado:** Retorna `200 OK` se for dono ou admin; `403 Forbidden` se tentar alterar outro cliente.

### Endpoint 4.3: `DELETE /clientes/{id}`
- **Contrato:** `DELETE /clientes/{id}`, `@RolesAllowed({"Administrador", "Usuario"})`.
- **Comportamento Observado:** Retorna `204 No Content`.
- **Impacto:** Se o cliente tiver pedidos, a exclusão falha no banco com FK violation ou apaga em cascata endereços vinculados a pedidos.

### Endpoint 4.4: `GET /clientes`
- **Contrato:** `GET /clientes`, `@RolesAllowed({"Administrador"})`.
- **Comportamento Observado:** Retorna `401 Unauthorized` sem token, `403 Forbidden` para cliente comum e `200 OK` para Administrador.

### Endpoint 4.5: `GET /clientes/{id}`
- **Contrato:** `GET /clientes/{id}`, `@RolesAllowed({"Administrador", "Usuario"})`.
- **Fluxo Interno:** Valida se o cliente autenticado é o dono do ID. Retorna `403` se tentar ler dados de outro cliente.

### Endpoint 4.6: `GET /clientes/search/{nome}`
- **Contrato:** `GET /clientes/search/{nome}`, `@RolesAllowed({"Administrador"})`. Retorna `200 OK`.

---

## 5. EnderecoResource (`/endereco`)

### Endpoints Auditados:
- `POST /endereco`: Cria endereço para o usuário logado (`jwt.getClaim("id")`). Retorna `201 Created`.
- `PUT /endereco/{id}`: Atualiza endereço. Valida se pertence ao usuário logado via `SecurityService`. Retorna `200 OK`.
- `DELETE /endereco/{id}`: Remove endereço. Retorna `204 No Content`.
  - **Bug Confirmado:** Se o endereço foi utilizado em um pedido existente (`pedido.id_endereco_entrega`), a exclusão gera erro 500 (`PSQLException: 23503 foreign key violation`).
- `GET /endereco/{id}`: Detalha endereço do usuário logado.
- `GET /endereco`: Lista todos os endereços do sistema (exige `Administrador`).
- `GET /endereco/find/cep/{cep}`: Busca técnica por CEP (exige `Administrador`).
- `GET /endereco/find/rua/{rua}`: Busca técnica por Rua (exige `Administrador`).
- **Divergências Críticas:**
  - **Nome no Singular:** O path é `@Path("/endereco")` enquanto todos os outros resources usam plural (`/usuarios`, `/clientes`, `/cafes`).
  - **NPE Potencial:** Em `EnderecoServiceImpl:68`, faz `jwt.getClaim("id").toString()`. Se o claim não existir, gera NPE antes da verificação condicional.
  - **Falta de Endpoint de Coleção Pessoal:** O frontend precisa de `GET /clientes/me/enderecos` ou `GET /usuarios/{id}/enderecos`, mas o método está comentado no controller.

---

## 6. EstadoResource (`/estados`)

### Endpoints Auditados:
- `GET /estados`: Público (`@PermitAll`). Retorna lista dos 27 estados em ordem de inserção com HTTP `200 OK`.
- `GET /estados/{id}`: Público. Retorna dados do estado ou `404 Not Found`.
- `GET /estados/find/{nome}`: Público. Retorna lista de estados por nome.
- `POST /estados`: Restrito a `@RolesAllowed({"Administrador"})`. Retorna `201 Created` sem header `Location`.
- `PUT /estados/{id}`: Restrito a `Administrador`. Retorna `200 OK`.
- `DELETE /estados/{id}`: Restrito a `Administrador`. Retorna `204 No Content`.
- **Divergências:** Falta de validação `@Valid` no `POST` e `PUT`. Nomenclatura do endpoint de busca com `/find/{nome}`.

---

## 7. MarcaResource (`/marcas`)

### Endpoints Auditados:
- `GET /marcas`, `GET /marcas/{id}`, `GET /marcas/search/{nome}`: Públicos. Retornam `200 OK`.
- `POST /marcas`, `PUT /marcas/{id}`, `DELETE /marcas/{id}`: Restritos com `@RolesAllowed({"Administrador"})`.
- **Comportamento Observado:** Este resource possui controle de segurança ativo (diferente de `CafeResource` e `CategoriaDoCafeResource`, onde as anotações foram comentadas).

---

## 8. MunicipioResource (`/municipios`)

### Endpoints Auditados:
- `GET /municipios`: Aberto. Retorna todos os municípios.
- `GET /municipios/{id}`: Aberto.
- `GET /municipios/find/{nome}`: Aberto.
- `POST /municipios`: `@RolesAllowed({"Administrador"})` COMENTADO! Aberto publicamente.
- `PUT /municipios/{id}`: `@RolesAllowed` COMENTADO! Aberto publicamente.
- `DELETE /municipios/{id}`: `@RolesAllowed` COMENTADO! Aberto publicamente.
- **Divergências:**
  - Falta de `@Valid` no DTO do controller.
  - Segurança desativada por comentário nas operações de mutação.
  - Inconsistência com `EstadoResource` (onde a segurança está ativa).

---

## 9. PagamentoResource (`/pagamentos`)

### Endpoint 9.1: `POST /pagamentos/cartao`
- **Contrato:** `@RolesAllowed({"Usuario", "Administrador"})`, `@Valid PagamentoCartaoDTO`.
- **Fluxo Interno:**
  1. `validarPedido`: Verifica se o pedido existe, se `totalPedido > 0` e se o status é `AGUARDANDO_PAGAMENTO`. Lança `BadRequestException` se inválido.
  2. `securityService.validarPermissao(pedido.getUsuario())`: Garante que quem paga é o dono do pedido ou admin.
  3. Se `cvv == "000"`, simula recusa lançando `BadRequestException`.
  4. Cria `PagamentoCartao`, mascara número do cartão, marca `confirmado = true`, altera `pedido.status = PAGO`.
- **Comportamento Observado:**
  - Sucesso: `201 Created` sem header `Location`. Status do pedido atualizado para `PAGO`.
  - Tentativa de pagar duas vezes: Retorna **`HTTP 400 Bad Request` com corpo vazio**.
  - Campo ausente (ex.: `validade`): Retorna `HTTP 400` com JSON RFC 7807 (via `ConstraintViolationExceptionMapper`).

### Endpoint 9.2: `POST /pagamentos/pix`
- **Fluxo Interno:** Gera chave UUID aleatória, define expiração em 30 min, salva com `confirmado = false`.
- **Comportamento Observado:** `201 Created`. Status do pedido permanece em `AGUARDANDO_PAGAMENTO`.
- **Lacuna de Domínio:** Não existe rota de webhook para confirmar que o Pix foi pago!

### Endpoint 9.3: `POST /pagamentos/boleto`
- **Fluxo Interno:** Gera linha digitável simulada, vencimento em D+3, salva com `confirmado = false`.
- **Comportamento Observado:** `201 Created`. Sem webhook de liquidação bancária.

---

## 10. PedidoResource (`/pedidos`)

### Endpoint 10.1: `POST /pedidos`
- **Contrato:**
  - Classe anotada com `@Authenticated`, mas o método tem `@PermitAll`!
  - `@Valid PedidoDTO`: `{"idEnderecoEntrega": 1, "itens": [{"idCafe": 1, "quantidade": 2}]}`
- **Fluxo Interno:**
  1. Extrai claim `id` do JWT. Se nulo, lança `ForbiddenException`.
  2. Valida se o endereço informado pertence ao usuário logado.
  3. Calcula frete dinamicamente via `FreteService`.
  4. Para cada item: busca o café, executa baixa de estoque imediatamente (`estoqueService.baixarEstoque`), extrai snapshot do preço atual do café, soma total.
  5. Salva pedido com status `AGUARDANDO_PAGAMENTO`.
- **Comportamento Observado:**
  - Cria pedido com sucesso, atualiza estoque do café.
  - Se chamado sem token: Retorna `403 Forbidden` com mensagem amigável em vez de `401 Unauthorized` por causa do `@PermitAll` no método.
  - Retorna `201 Created` sem header `Location`.

### Endpoint 10.2: `GET /pedidos`
- **Contrato:** `@RolesAllowed({"Administrador"})`.
- **Comportamento Observado:** `401 Unauthorized` sem token, `403 Forbidden` para cliente comum, `200 OK` para Administrador.

### Endpoint 10.3: `GET /pedidos/{id}`
- **Contrato:** `@RolesAllowed({"Administrador", "Usuario"})`.
- **Fluxo Interno:** `securityService.validarPermissao(pedido.getUsuario())` impede que um cliente veja pedidos de outro. Retorna `403 Forbidden` se não for dono.

### Endpoint 10.4: `GET /pedidos/usuario/{idUsuario}`
- **Contrato:** `@RolesAllowed({"Administrador", "Usuario"})`.
- **Divergência REST:** O caminho `/pedidos/usuario/{idUsuario}` foge da hierarquia RESTful canônica. O correto seria `/usuarios/{id}/pedidos` ou `/clientes/{id}/pedidos`, ou simplesmente `/pedidos/me` para a listagem do próprio usuário.

---

## 11. RegiaoResource (`/regioes`)

### Endpoint 11.1: `GET /regioes`
- **Contrato:** `@PermitAll`, Produces `application/json`.
- **Fluxo Interno:** Retorna `Regiao.values()` (enum com id e label: Norte, Nordeste, Centro-Oeste, Sudeste, Sul).
- **Comportamento Observado:** `200 OK`.
- **Consumo pelo Angular:** Consumido por `regiao.service.ts` para carregar o combo de regiões no formulário de Estado.

---

## 12. TelefoneResource (`/telefones`)

### Endpoints Auditados:
- `POST /telefones`: Cria telefone para o usuário identificado no token JWT. Retorna `201 Created`.
- `PUT /telefones/{id}`: Atualiza telefone. Valida se pertence ao usuário do token. Retorna `200 OK`.
- `DELETE /telefones/{id}`: Exclui telefone. Retorna `204 No Content`.
- `GET /telefones/{id}`: Detalha telefone se for dono ou admin. Retorna `200 OK`.
- `GET /telefones`: Lista todos os telefones do sistema (exige `Administrador`).
- `GET /telefones/usuario/{idUsuario}`: Lista telefones de um usuário (com validação de dono/admin).
- **Divergências:**
  - O sub-recurso telefone deveria estar aninhado à conta do usuário (`/usuarios/{id}/telefones` ou `/clientes/me/telefones`).

---

## 13. UsuarioResource (`/usuarios`)

### Endpoint 13.1: `GET /usuarios`
- **Contrato:** Sem anotações de segurança! `@Produces(MediaType.APPLICATION_JSON)`.
- **Fluxo Interno:** `UsuarioServiceImpl.findAll()` -> `UsuarioRepository.listAll()`.
- **Comportamento Observado:**
  - **Vulnerabilidade Crítica Confirmada:** Retorna `HTTP 200 OK` **sem autenticação nenhuma**, listando todos os usuários do sistema com CPF, data de nascimento, endereços, telefones e perfis (incluindo o perfil do Admin).

### Endpoint 13.2: `GET /usuarios/{id}` e `GET /usuarios/find/{nome}`
- **Comportamento Observado:** Públicos e desprotegidos. Qualquer um consulta qualquer usuário por ID ou nome.

### Endpoint 13.3: `POST /usuarios`
- **Contrato:** Sem anotações de segurança! **Sem anotação `@Valid` no parâmetro `UsuarioDTO dto`!**
- **Fluxo Interno:** Cria usuário com o `idPerfil` recebido no JSON.
- **Comportamento Observado:**
  - Se enviado payload vazio `{}`: Retorna **`HTTP 500 Internal Server Error`** com `NullPointerException` (porque sem `@Valid` o código tenta fazer hash de senha nula).
  - Se enviado payload com `idPerfil: 1`: **Cria um novo Administrador no sistema sem exigir login ou privilégios!**

### Endpoint 13.4: `PUT /usuarios/{id}`
- **Contrato:** Sem anotações de segurança! Sem `@Valid`.
- **Comportamento Observado:**
  - Qualquer pessoa pode alterar dados de qualquer usuário e passar `idPerfil: 1`, **escalando privilégios diretamente para Administrador!**

### Endpoint 13.5: `DELETE /usuarios/{id}`
- **Contrato:** `@RolesAllowed("Administrador")` COMENTADO!
- **Comportamento Observado:** Qualquer pessoa anônima pode apagar qualquer usuário do sistema via `DELETE /usuarios/{id}`.
