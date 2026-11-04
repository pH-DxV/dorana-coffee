# Impacto das Alterações da API no Frontend Angular

**Data:** 21/10/2026  
**Público-Alvo:** Desenvolvedores Frontend Angular  
**Status:** Guia de Migração de Contratos e Serviços  

---

## 1. Visão Geral das Mudanças de Contrato

A refatoração da API elimina vulnerabilidades críticas e implementa o padrão REST canônico. Abaixo estão listadas todas as rotas alteradas, novos campos e mudanças de status codes que impactam os serviços e componentes do Angular (localizados em `frontend/src/app/services/` e `models/`).

---

## 2. Mapa Detalhado de Endpoints Alterados

| Recurso | Endpoint Anterior | Endpoint Novo (REST Canônico) | Mudança no Angular (`*.service.ts`) |
|---|---|---|---|
| **Autenticação (Login)** | `POST /auth/login` (Token apenas no Header `Authorization`, corpo vazio) | `POST /auth/login` (Token no Header **E** objeto `AuthDTOResponse` no corpo com `token`, `id`, `nome`, `email`, `perfil`) | Simplifica o `auth.service.ts`: agora pode ler o perfil e ID diretamente do body sem decodificar JWT manualmente. |
| **Login com Erro** | Retornava `HTTP 204 No Content` | Retorna **`HTTP 401 Unauthorized`** com RFC 7807 (`detail`: "Credenciais inválidas") | Interceptor HTTP do Angular deve capturar status `401` em vez de checar `204`. |
| **Cadastro de Clientes** | `POST /usuarios` com `idPerfil` | **`POST /clientes`** (autocadastro público, sem campo de perfil) | O formulário de registro de novo cliente deve apontar para `/clientes` e enviar `email` obrigatório. |
| **Gestão Administrativa** | `POST /usuarios` com `idPerfil: 1` | **`POST /administradores`** | Área administrativa passa a gerenciar funcionários em `/administradores`. |
| **Perfil Pessoal** | Inexistente (usava `GET /usuarios/{id}`) | **`GET /usuarios/me`** e **`PUT /usuarios/me/senha`** | Tela "Minha Conta / Meu Perfil" passa a chamar `/usuarios/me` sem precisar passar ID na URL. |
| **Endereços** | `POST /endereco`, `GET /endereco` (Singular) | **`POST /enderecos`**, **`GET /enderecos`** (Plural) | Atualizar URL base em `endereco.service.ts` de `/endereco` para `/enderecos`. |
| **Busca de Cafés por Nome** | `GET /cafes/search/nome/{nome}` | **`GET /cafes?nome={nome}`** | `cafe.service.ts`: substituir interpolação de path por `HttpParams({ fromObject: { nome } })`. |
| **Filtro de Café por Pontuação**| `GET /cafes/search/pontuacao?min=&max=` | **`GET /cafes?minPontuacao={min}&maxPontuacao={max}`** | `cafe.service.ts`: unificado no endpoint raiz `/cafes`. |
| **Busca de Categorias** | `GET /categorias/search/{nome}` | **`GET /categorias?nome={nome}`** | `categoria-do-cafe.service.ts`: migrar para query parameter `?nome=`. |
| **Busca de Marcas** | `GET /marcas/search/{nome}` | **`GET /marcas?nome={nome}`** | `marca.service.ts`: migrar para query parameter `?nome=`. |
| **Busca de Estados** | `GET /estados/find/{nome}` | **`GET /estados?nome={nome}`** | `estado.service.ts`: migrar de `/find/{nome}` para `?nome=`. |
| **Busca de Municípios** | `GET /municipios/find/{nome}` | **`GET /municipios?nome={nome}`** | `municipio.service.ts`: migrar de `/find/{nome}` para `?nome=`. |
| **Pedidos por Usuário** | `GET /pedidos/usuario/{idUsuario}` | **`GET /pedidos?idUsuario={id}`** ou **`GET /clientes/me/pedidos`** | Tela "Meus Pedidos" passa a consultar `/clientes/me/pedidos`. |
| **Cancelamento de Pedido** | Inexistente | **`POST /pedidos/{id}/cancelamento`** | Novo botão na tela de detalhes do pedido para cancelar pedidos não faturados. |

---

## 3. Mudanças nos Modelos TypeScript (`frontend/src/app/models/`)

### 3.1 `usuario.model.ts` e `cliente.model.ts`
- **Adicionado:** `email: string` (obrigatório em cadastro e exibição).
- **Removido:** `idPerfil` (o perfil é derivado do tipo da conta).
- **Criado:** `administrador.model.ts` com o campo adicional `cargo?: string`.

### 3.2 `cafe.model.ts`
- **Adicionado:** `moagensPermitidas: string[]` (`EM_GRAOS`, `MOIDO_FILTRO`, `MOIDO_ESPRESSO`, `MOIDO_PRENSA_FRANCESA`).
- **Adicionado:** `regiaoDeOrigem?: RegiaoDeOrigem` (terroir de origem do grão).
- **Adicionado:** `ativo: boolean` (para controle de catálogo ativo/inativo).
- **Modificado:** `pesoGramas: number` (peso padronizado em gramas).

### 3.3 `pedido.model.ts`
- **Adicionado no Item:** `moagem: string` (tipo de moagem selecionada pelo cliente na compra).
