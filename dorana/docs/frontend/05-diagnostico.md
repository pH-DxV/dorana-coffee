# 05 - Relatório de Diagnóstico e Consolidação

**Data da Análise:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 3 (Consolidação do Diagnóstico Pré-Operatório)  

---

## 1. Resumo Executivo (Máx. 15 Linhas)

O front-end é uma aplicação Angular 21 Standalone utilizando Angular Material e Angular SSR. Hoje, a aplicação implementa 4 telas de retaguarda administrativa (Estados, Municípios, Cafés e Usuários) que seguem um padrão consistente de tabela com filtro client-side e formulários reativos vinculados a resolvers. No entanto, o front-end encontra-se inoperante em produção e desenvolvimento: o `build` falha devido a incompatibilidade das rotas dinâmicas com o pré-renderizador SSR, e os testes unitários falham por desconfiguração do runner Vitest. Além disso, a aplicação não possui camada de autenticação (telas de login, token JWT e interceptors), gerando recusa de requisições (`401 Unauthorized`) pela API refatorada. Toda a jornada de e-commerce do cliente (catálogo vitrine, carrinho, checkout e acompanhamento de pedidos) e a gestão de marcas e categorias ainda não foram construídas.

---

## 2. Problemas Encontrados no Front-end Atual

```
[CRÍTICO] [CONFIRMADO] Falha no build da aplicação devido a Prerender em rotas dinâmicas
Onde: src/app/app.routes.server.ts:3-8
Comportamento atual: serverRoutes define RenderMode.Prerender para '**', falhando com erro fatal ao tentar pré-renderizar rotas com parâmetros (:id) como 'usuarios/edit/:id'.
Comportamento esperado: Rotas com parâmetros dinâmicos devem usar RenderMode.Server ou RenderMode.Client, ou a aplicação SSR deve configurar rotas estáticas adequadas.
Impacto: Impossibilidade de gerar bundle de produção (npm run build quebra).
Correção proposta: Ajustar app.routes.server.ts para definir RenderMode.Server nas rotas dinâmicas ou desabilitar o prerender em rotas de edição.
Esforço: P
```

```
[CRÍTICO] [CONFIRMADO] Ausência do provider de HttpClient no bootstrap da aplicação
Onde: src/app/app.config.ts:5-10
Comportamento atual: appConfig provê apenas provideBrowserGlobalErrorListeners() e provideRouter(routes).
Comportamento esperado: Deve fornecer provideHttpClient(withFetch(), withInterceptors([...])) para permitir a injeção do HttpClient nos serviços.
Impacto: Erro fatal 'NullInjectorError: No provider for HttpClient' ao carregar qualquer tela que realize chamadas HTTP.
Correção proposta: Adicionar provideHttpClient() aos providers do appConfig.
Esforço: P
```

```
[CRÍTICO] [CONFIRMADO] Ausência total de camada de autenticação e interceptor JWT
Onde: src/app/
Comportamento atual: Não existe serviço de autenticação, tela de login nem interceptor HTTP para enviar token Bearer.
Comportamento esperado: Armazenamento seguro de token JWT após login e interceptor anexando o header 'Authorization: Bearer <token>' para a API.
Impacto: Quase todos os endpoints da API recusam requisições com HTTP 401 Unauthorized.
Correção proposta: Criar AuthService, AuthInterceptor e tela de login nos moldes padrão.
Esforço: M
```

```
[ALTO] [CONFIRMADO] Cadastro de usuário sem o campo obrigatório 'email'
Onde: src/app/components/usuarios/usuario-form/usuario-form.ts:56-66
Comportamento atual: O formulário de usuário não instancia nem envia o campo 'email'.
Comportamento esperado: O backend exige email obrigatório (@NotBlank, @Email).
Impacto: Tentativas de salvar ou atualizar usuários são rejeitadas pelo backend com HTTP 400 Bad Request.
Correção proposta: Adicionar o campo email no formGroup e no template HTML do usuario-form.
Esforço: P
```

```
[ALTO] [CONFIRMADO] Chamadas a endpoints legados de busca por nome
Onde: src/app/services/usuario.service.ts:25-27
Comportamento atual: Executa requisição para '/usuarios/find/{nome}'.
Comportamento esperado: Backend refatorado utiliza query parameters '/usuarios?nome={nome}'.
Impacto: Busca de usuários falha com HTTP 404 Not Found.
Correção proposta: Atualizar método para usar HttpParams com ?nome=.
Esforço: P
```

```
[MÉDIO] [CONFIRMADO] Falha na execução da suíte de testes unitários com Vitest
Onde: src/app/app.spec.ts:4
Comportamento atual: O teste usa funções globais Jasmine/Jest ('describe', 'it') sem configuração de globais no Vitest, disparando 'ReferenceError: describe is not defined'.
Comportamento esperado: O executor deve rodar e passar o teste de fumaça inicial.
Impacto: 'npm test' quebra imediatamente.
Correção proposta: Importar 'describe', 'it', 'expect' diretamente de 'vitest' ou habilitar globals no vitest config.
Esforço: P
```

---

## 3. Divergências de Padrão que Precisam de Decisão

1. **Paginação: No Cliente (In-Memory) vs. No Servidor (Server-Side):**
   - *Situação:* Os componentes atuais buscam `findAll()` de uma só vez da API e delegam a paginação para o `MatPaginator` + `MatTableDataSource` no browser.
   - *Opção A (Recomendada):* Manter a paginação no cliente por enquanto para as telas administrativas existentes, preservando 100% da identidade dos módulos de referência.
   - *Opção B:* Migrar os serviços para enviar `?page=...&size=...` caso o backend já suporte os parâmetros de paginação.

2. **Navegação Global e Layout:**
   - *Situação:* O `app.html` contém apenas `<router-outlet></router-outlet>`, forçando o usuário a digitar URLs manualmente na barra de navegação do browser.
   - *Opção A (Recomendada):* Criar um componente de cabeçalho global (`NavbarComponent`) discreto com os links das telas principais e estado de login/logout.
   - *Opção B:* Não criar menu global e manter cada tela totalmente isolada.

---

## 4. Dúvidas de Negócio e de Escopo

1. **Prioridade de Entrega:**
   - O escopo principal imediato deve focar em **(A)** Estabilizar a aplicação (corrigir build SSR, `app.config`, email de usuário e testes) e completar as telas administrativas faltantes (Marcas e Categorias)? Ou **(B)** Iniciar a criação do fluxo completo de compras de e-commerce (vitrine pública, carrinho e checkout)?
   - *(Recomendação: Opção A primeiro, garantindo que o back-office existente funcione de ponta a ponta e passe no build/testes).*

2. **Autenticação:**
   - Devemos criar uma tela de Login (`/login`) no mesmo padrão visual do formulário de usuário para autenticar como Administrador e permitir testar as chamadas protegidas da API?
   - *(Recomendação: Sim, com armazenamento do token JWT e injeção via `HttpInterceptor`).*
