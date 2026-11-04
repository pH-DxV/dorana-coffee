# 01 - Reconhecimento da Aplicação Front-end

**Data da Análise:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 0 (Reconhecimento de Baseline - Somente Leitura)  

---

## 1. Stack Tecnológico e Versões

Todas as informações foram obtidas diretamente de [`package.json`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/package.json), [`angular.json`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/angular.json) e [`tsconfig.json`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/tsconfig.json):

- **Framework:** Angular 21 (`@angular/core`: `^21.1.0`, `@angular/cli`: `^21.1.4`, `@angular/ssr`: `^21.1.4`)
- **Linguagem:** TypeScript 5.9.2 (`typescript`: `~5.9.2`). Todos os arquivos de lógica são estritamente `.ts`; não há arquivos legados em `.js` no código-fonte.
- **RxJS:** `~7.8.0`
- **Gerenciador de Pacotes:** `npm@10.8.2`
- **Biblioteca de Componentes UI:** Angular Material 21 (`@angular/material`: `^21.2.14`, `@angular/cdk`: `^21.2.14`).
- **Estilo de Componentes:** Componentes **Standalone** (`imports: [...]` explícito em `@Component`, sem nenhum `NgModule`).
- **Renderização e Servidor:** Angular SSR configurado com `@angular/ssr` e `express` (`^5.1.0`), builder `@angular/build:application`.
- **Testes Unitários:** Configurado com `vitest` (`^4.0.8`) e `jsdom` (`^27.1.0`), builder `@angular/build:unit-test`.

---

## 2. Estrutura de Pastas e Organização

A aplicação em `src/app/` está organizada por camadas/tipos técnicos:

```text
src/
├── app/
│   ├── components/            # Telas/Features divididas por entidade
│   │   ├── cafes/             # cafe-form, cafe-list
│   │   ├── estados/           # estado-form, estado-list
│   │   ├── municipios/        # municipio-form, municipio-list
│   │   └── usuarios/          # usuario-form, usuario-list
│   ├── models/                # Interfaces/classes de modelo de dados (13 arquivos)
│   ├── resolvers/             # Resolvers de rotas para pré-carregamento por ID
│   ├── services/              # Serviços de comunicação HTTP com a API REST (7 arquivos)
│   ├── app.config.ts          # Configuração da aplicação (provideRouter, provideBrowserGlobalErrorListeners)
│   ├── app.config.server.ts   # Configuração de SSR
│   ├── app.routes.ts          # Definição das rotas de navegação da aplicação
│   ├── app.routes.server.ts   # Configuração de modo de renderização SSR
│   └── app.ts / app.html      # Componente raiz contendo apenas <router-outlet>
├── material-theme.scss        # Tema customizado Material M3/M2
└── styles.css                 # Estilos globais
```

---

## 3. Comunicação com a API e Ambientes

- **Ambiente e URL Base:** Não existem arquivos de ambiente (`src/environments/environment.ts` ou `environment.prod.ts`). Não há proxy reverso (`proxy.conf.json`) configurado.
- **Hardcoding de URL:** Cada serviço (`*.service.ts`) declara a URL diretamente em uma variável privada interna:
  ```typescript
  private apiUrl = 'http://localhost:8080/cafes'; // ou /estados, /usuarios, etc.
  ```
- **HttpClient:** `provideHttpClient()` **não** está presente no `app.config.ts`, gerando falhas em runtime caso instanciado sem provider global.

---

## 4. Autenticação, Estado e Segurança

- **Autenticação:** Não existe tela de login, cadastro público, serviço de autenticação (`AuthService`), armazenamento de token JWT (`localStorage`/`sessionStorage`) nem `HttpInterceptor` para inclusão do header `Authorization: Bearer <token>`.
- **Guards:** Não existem route guards (`CanActivate`, `CanActivateChild` ou funções `canActivateFn`). Todas as rotas estão abertas.
- **Gerenciamento de Estado:** Não há NgRx nem Signals store centralizado. Os componentes consomem diretamente os Observables retornados pelos serviços de domínio via `subscribe()`.
- **Layout Global:** O `app.html` possui apenas `<router-outlet></router-outlet>`. Não há menu global persistente (sidebar/navbar), cabeçalho com usuário logado ou rodapé.

---

## 5. Diagnóstico de Execução (Build e Testes)

### 5.1. Status do `npm run build`
- **Resultado:** **Falha (Exit code 1)**.
- **Causa Raiz:** O Angular SSR está com a regra genérica `path: '**', renderMode: RenderMode.Prerender` em [`app.routes.server.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/app.routes.server.ts). Como as rotas `*/edit/:id` contêm parâmetros de rota dinâmicos, o prerender falha por exigir a função `getPrerenderParams` ou o modo `RenderMode.Server` / `RenderMode.Client`.
- **Aviso de Orçamento:** O bundle inicial gerou `726.14 kB`, excedendo o orçamento de warning estipulado em `500.00 kB` no `angular.json`.

### 5.2. Status do `npm test`
- **Resultado:** **Falha**.
- **Causa Raiz:** O arquivo único [`app.spec.ts`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/app.spec.ts) usa as funções globais do Jasmine/Jest (`describe`, `it`, `beforeEach`), mas o runner configurado é o `vitest` sem os tipos/globais ativados (`describe is not defined`). Não há testes de componentes ou serviços criados.
