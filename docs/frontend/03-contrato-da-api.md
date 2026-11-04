# 03 - Contrato da API REST Consumido pelo Front-end

**Data da Análise:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 2 (Mapeamento do Contrato da API Refatorada vs. Front-end)  

---

## 1. Mapeamento de Recursos e Endpoints da API Refatorada

A tabela abaixo detalha todos os endpoints disponíveis na API Quarkus refatorada, o método HTTP, autorização exigida e o status de compatibilidade com o front-end atual:

| Recurso | Método & Rota | Roles Autorizadas | Query Params / Body | Status Front-end Atual |
|---|---|---|---|---|
| **Auth** | `POST /auth/login` | Público | Body: `{ login, senha }` | **Inexistente** (não há tela de login nem serviço de auth) |
| **Cafés** | `GET /cafes` | Público / User | `?nome=...` | **Incompatível** (front busca sem query param, busca por nome usava rota antiga) |
| **Cafés** | `GET /cafes/{id}` | Público / User | Path: `{id}` | Implementado em `CafeService` |
| **Cafés** | `POST /cafes` | `Administrador` | Body: `CafeDTO` | Implementado em `CafeFormComponent` |
| **Cafés** | `PUT /cafes/{id}` | `Administrador` | Path + Body: `CafeDTO` | Implementado em `CafeFormComponent` |
| **Cafés** | `DELETE /cafes/{id}`| `Administrador` | Path: `{id}` | Implementado em `CafeListComponent` |
| **Categorias** | `GET /categorias` | Público / User | `?nome=...` | Implementado em `CategoriaDoCafeService` |
| **Categorias** | `POST, PUT, DELETE`| `Administrador`| CRUD completo | **Inexistente no Front** (só existe o service de leitura) |
| **Marcas** | `GET /marcas` | Público / User | `?nome=...` | Implementado em `MarcaService` |
| **Marcas** | `POST, PUT, DELETE`| `Administrador`| CRUD completo | **Inexistente no Front** (só existe o service de leitura) |
| **Estados** | `GET /estados` | Público / User | `?nome=...` | Implementado em `EstadoService` e telas |
| **Estados** | `POST, PUT, DELETE`| `Administrador`| CRUD completo | Implementado em `EstadoFormComponent` e telas |
| **Municípios** | `GET /municipios` | Público / User | `?nome=...` | Implementado em `MunicipioService` e telas |
| **Municípios** | `POST, PUT, DELETE`| `Administrador`| CRUD completo | Implementado em `MunicipioFormComponent` e telas |
| **Clientes** | `POST /clientes` | Público | Body: `ClienteDTO` (com `email`) | **Inexistente no Front** |
| **Clientes** | `GET /clientes/{id}`| `Administrador` | Path: `{id}` | **Inexistente no Front** |
| **Usuários** | `GET /usuarios` | `Administrador` | Listagem geral | Implementado em `UsuarioService` e telas |
| **Usuários** | `GET /usuarios/me`| Autenticado | Dados do usuário logado | **Inexistente no Front** |
| **Usuários** | `PUT /usuarios/me/senha`| Autenticado | Body: `AlterarSenhaDTO` | **Inexistente no Front** |
| **Usuários** | `POST /usuarios` | `Administrador` | Body: `UsuarioDTO` | Implementado, mas formulário envia payload legado sem `email` |
| **Pedidos** | `GET /pedidos` | `Administrador` | Listagem geral de pedidos | **Inexistente no Front** (sem telas/serviços) |
| **Pedidos** | `GET /pedidos/meus-pedidos` | `Cliente` | Pedidos do cliente logado | **Inexistente no Front** |
| **Pedidos** | `POST /pedidos` | `Cliente` | Body: `PedidoDTO` | **Inexistente no Front** |
| **Pedidos** | `PATCH /pedidos/{id}/cancelar`| `Cliente` | Path: `{id}` | **Inexistente no Front** |
| **Pagamentos** | `POST /pagamentos/cartao` | `Cliente` | Body: `PagamentoCartaoDTO` | **Inexistente no Front** |
| **Pagamentos** | `POST /pagamentos/boleto` | `Cliente` | Body: `PagamentoBoletoDTO` | **Inexistente no Front** |
| **Pagamentos** | `POST /pagamentos/pix` | `Cliente` | Body: `PagamentoPixDTO` | **Inexistente no Front** |
| **Estoque** | `GET /estoque/{idCafe}` | `Administrador` | Path: `{idCafe}` | **Inexistente no Front** |

---

## 2. Incompatibilidades Críticas Encontradas

1. **Email Ausente no Formulário de Usuário:**
   - O backend refatorado exige `email` como campo obrigatório (`@NotBlank`, `@Email`) no cadastro de usuários e clientes.
   - O [`usuario-form.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/components/usuarios/usuario-form/usuario-form.ts) não possui campo de email em seu formulário reativo nem no HTML. Qualquer submissão resulta em `HTTP 400 Bad Request`.

2. **Rotas de Busca Legadas:**
   - Em [`usuario.service.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/services/usuario.service.ts#L25), o método `findByNome` consome `${this.apiUrl}/find/${nome}`. O backend refatorado migrou todas as buscas para query parameters: `GET /usuarios?nome={nome}`.

3. **Autenticação e Cabeçalho JWT:**
   - Quase todos os endpoints de escrita da API exigem autenticação (`@RolesAllowed("Administrador")` ou `@RolesAllowed("Cliente")`).
   - O front-end atualmente não possui nenhum token JWT salvo nem `HttpInterceptor`. Toda requisição para rotas protegidas retornará `HTTP 401 Unauthorized`.
