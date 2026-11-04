# 06 - Plano de Implementação Incremental

**Data:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 4 (Planejamento Detalhado Pós-Aprovação)  

---

## 1. Diretrizes e Decisões Aprovadas

1. **Paginação:** Mantida a paginação client-side via `MatTableDataSource` + `MatPaginator` nas listagens para fidelidade total aos módulos de referência (`EstadoListComponent`).
2. **Navegação Global:** Inclusão de toolbar/navbar simples com links de navegação e status de autenticação no layout raiz.
3. **Escopo Prioritário (Opção A):**
   - **Etapa 1:** Estabilização da infraestrutura do front-end (correção do SSR build, injeção de `provideHttpClient`, ativação do Vitest para execução limpa de `npm test`).
   - **Etapa 2:** Correção de contratos e formulários legados (inclusão de `email` em `usuario-form`, correção de buscas com query param).
   - **Etapa 3:** Camada de Autenticação e Segurança (criação de `AuthService`, tela de `LoginComponent`, `AuthInterceptor` para cabeçalho JWT e proteção de rotas).
   - **Etapa 4:** Conclusão dos CRUDs Administrativos Faltantes (Módulos de Marcas e Categorias do Café, replicando fielmente os moldes de `EstadoList/Form`).
   - **Etapa 5:** Testes Unitários de Front-end e Validação de Build (`npm run build` e `npm test`).

---

## 2. Detalhamento das Etapas

### Etapa 1: Estabilização de Build e Infraestrutura
- **1.1 SSR Render Mode:** Atualizar [`app.routes.server.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/app.routes.server.ts) para configurar `RenderMode.Server` ou rotas específicas, eliminando o erro de pré-renderização em parâmetros `:id`.
- **1.2 Configuração Global HTTP:** Adicionar `provideHttpClient(withFetch(), withInterceptors([...]))` em [`app.config.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/app.config.ts).
- **1.3 Ajuste do Vitest:** Corrigir os imports em [`app.spec.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/app.spec.ts) para usar `{ describe, it, expect, beforeEach }` de `'vitest'`.
- *Critério de Aceite:* `npm run build` executa sem erros; `npm test` roda e passa o teste inicial.

### Etapa 2: Correção de Contratos Existentes
- **2.1 Campo Email em Usuários:**
  - Em `usuario.model.ts`: incluir `email: string`.
  - Em `usuario-form.ts`: adicionar o campo `email` no `FormGroup`.
  - Em `usuario-form.html`: adicionar o `<mat-form-field>` correspondente.
- **2.2 Query Params em Serviços:**
  - Atualizar `usuario.service.ts` e `cafe.service.ts` para enviar query parameters em conformidade com a API REST refatorada.
- *Critério de Aceite:* Usuários podem ser criados sem erro 400.

### Etapa 3: Autenticação JWT e Segurança
- **3.1 Modelo e Serviço:** Criar `auth.model.ts` e `auth.service.ts` para chamada a `POST http://localhost:8080/auth/login`, armazenando o token no `localStorage`.
- **3.2 Interceptor:** Criar `auth.interceptor.ts` que anexa `Authorization: Bearer <token>` em requisições destinadas à API.
- **3.3 Componente de Login:** Criar `login.component.ts/html/css` na pasta `components/auth/login` utilizando `<mat-card>`, inputs de login e senha, feedback via `MatSnackBar` e redirecionamento.
- **3.4 Navbar:** Adicionar cabeçalho em `app.html` com botões para as telas e botão de Login/Logout.
- *Critério de Aceite:* Usuário realiza login e o token é propagado nas requisições da aplicação.

### Etapa 4: Implementação das Features Faltantes (Marcas e Categorias)
- **4.1 Módulo de Marcas:**
  - Criar `marca-resolver.ts`.
  - Criar `marca-list` (tabela, filtro, paginação, exclusão).
  - Criar `marca-form` (formulário reativo com campos `nome`, `descricao`, feedback via `MatSnackBar`).
  - Registrar rotas `/marcas`, `/marcas/new`, `/marcas/edit/:id`.
- **4.2 Módulo de Categorias:**
  - Criar `categoria-resolver.ts`.
  - Criar `categoria-list` e `categoria-form` espelhando `Marca`.
  - Registrar rotas `/categorias`, `/categorias/new`, `/categorias/edit/:id`.
- *Critério de Aceite:* Marcas e Categorias contam com CRUD completo operacional seguindo 100% o estilo das telas de referência.

### Etapa 5: Testes Automatizados e Documentação Final
- **5.1 Testes Unitários de Serviços e Componentes:**
  - Testes para `AuthService`, `EstadoService`, `MarcaService` e `CategoriaDoCafeService`.
  - Testes para os componentes de listagem e formulário.
- **5.2 Validação Final:**
  - Execução de `npm test` e `npm run build`.
  - Emissão dos relatórios `07-relatorio-de-testes.md` e `99-relatorio-final.md`.
