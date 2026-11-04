# 04 - Matriz de Cobertura e Lacunas (Front-end vs. Back-end)

**Data da Análise:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 2 (Cruzamento Completo de Recursos)  

---

## 1. Matriz de Cobertura por Recurso

A tabela a seguir apresenta o status de implementação no front-end para cada recurso exposto pelo back-end:

| Recurso | Listar | Detalhar | Criar | Editar | Excluir | Serviço | Modelo | Rotas | Testes |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **Estados** | Sim | Sim (via resolver) | Sim | Sim | Sim | Sim | Sim | Sim | Não |
| **Municípios** | Sim | Sim (via resolver) | Sim | Sim | Sim | Sim | Sim | Sim | Não |
| **Cafés** | Sim | Sim (via resolver) | Sim | Sim | Sim | Sim | Sim | Sim | Não |
| **Usuários** | Sim | Sim (via resolver) | Parcial* | Parcial* | Sim | Sim | Sim | Sim | Não |
| **Marcas** | Não | Não | Não | Não | Não | Sim | Sim | Não | Não |
| **Categorias** | Não | Não | Não | Não | Não | Sim | Sim | Não | Não |
| **Clientes** | Não | Não | Não | Não | Não | Não | Não | Não | Não |
| **Pedidos** | Não | Não | Não | Não | Não | Não | Não | Não | Não |
| **Pagamentos** | Não | Não | Não | Não | Não | Não | Não | Não | Não |
| **Estoque** | Não | Não | Não | Não | Não | Não | Não | Não | Não |
| **Autenticação**| Não | Não | Não | Não | Não | Não | Não | Não | Não |

*\*Parcial:* O formulário de usuário existe, mas falha ao submeter porque o campo `email` não foi incluído na interface nem no payload.

---

## 2. Lacunas Funcionais por Domínio de Aplicação

### 2.1. Área Pública e do Cliente (E-Commerce) - Faltando Integralmente
- **Autenticação de Usuários:** Falta tela de Login (`/login`) e tela de Cadastro de Cliente (`/registro`).
- **Catálogo Vitrine:** A tela atual de cafés é uma tabela administrativa crua. Não existe vitrine de produtos (cards de café com foto, preço, torra, notas sensoriais e botão "Adicionar ao Carrinho").
- **Carrinho de Compras:** Não existe serviço de carrinho em memória (`CartService`) nem tela de checkout (`/carrinho`, `/checkout`).
- **Meus Pedidos:** Não existe tela para o cliente visualizar seus pedidos realizados (`/meus-pedidos`) nem efetuar pagamento.

### 2.2. Área Administrativa (Back-office) - Lacunas
- **CRUD de Marcas:** Serviço existe (`marca.service.ts`), mas faltam componentes de listagem e formulário (`marca-list`, `marca-form`).
- **CRUD de Categorias:** Serviço existe (`categoria-do-cafe.service.ts`), mas faltam componentes de listagem e formulário (`categoria-list`, `categoria-form`).
- **Gestão de Pedidos:** Falta tela administrativa para listar pedidos de clientes e atualizar status de entrega.
- **Gestão de Estoque:** Falta visualização e ajuste de saldo de estoque por produto.

### 2.3. Recursos Transversais de Arquitetura - Faltando Integralmente
- **Layout Persistente:** Navbar com links de navegação ("Cafés", "Estados", "Municípios", "Login", "Carrinho").
- **`AuthInterceptor`:** Injeção automática do cabeçalho `Authorization: Bearer <token>` em todas as requisições HTTP para a API.
- **`AuthGuard`:** Proteção das rotas administrativas (`/estados`, `/usuarios`, etc.) impedindo acesso sem login de administrador.
