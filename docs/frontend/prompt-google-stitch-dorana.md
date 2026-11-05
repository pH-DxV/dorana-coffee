# Prompt Mestre para Google Stitch — Dorana Coffee Shop (UI/UX & Design System)

Abaixo encontra-se o prompt completo, estruturado e detalhado, pronto para ser copiado e colado diretamente no **Google Stitch** para geração das interfaces da aplicação **Dorana Coffee Shop**.

---

```markdown
Atue como um Product Designer Sênior e Especialista em Design Systems Web (Angular Material M3) para marcas de luxo e cafés de especialidade. Projete um sistema web completo para a **Dorana Coffee Shop** — uma plataforma de e-commerce e gestão administrativa (Back-Office) de grãos de cafés especiais, gourmet e microlotes artesanais.

O design deve ser moderno, minimalista, editorial, acolhedor e altamente funcional, priorizando a experiência Desktop (1280px–1440px, grid de 12 colunas) com adaptação fluida e responsiva para Tablets (8 colunas) e Mobile (4 colunas). Utilize estritamente os padrões de componentes do **Angular Material (Material Design 3)** customizados com a identidade visual "Torrefação Nobre" especificada abaixo.

---

### 1. IDENTIDADE VISUAL, MARCA E PALETA DE CORES (OBRIGATÓRIO)

- **Marca Oficial:** `Dorana COFFEE SHOP`
  - **Logotipo:** A palavra **Dorana** deve ter destaque principal em tipografia serifada elegante (*Playfair Display*, SemiBold/Bold), acompanhada abaixo ou ao lado pelo complemento **COFFEE SHOP** em fonte sem serifa (*Plus Jakarta Sans*, SemiBold, caixa alta, tamanho menor e espaçamento expandido `letter-spacing: 0.22em`).
- **Tipografia:**
  - **Títulos e Cabeçalhos (`Display` e `Headline`):** `Playfair Display` (pesos 500 e 600) — transmite tradição, curadoria e sofisticação gastronômica.
  - **Corpo de Texto, Tabelas, Inputs, Labels e Botões (`Body` e `Label`):** `Plus Jakarta Sans` (pesos 400, 600 e 700) — garante legibilidade técnica milimétrica para preços, pesos, datas e especificações sensoriais.
- **Paleta de Cores (Códigos HEX Estritos):**
  - **Primary (`#130804` - Café Ébano):** Botões primários (`mat-flat-button`), textos principais, títulos e elementos de âncora visual.
  - **Primary Container (`#2C1E18` - Tostado Profundo):** Sidebar administrativa, cabeçalhos de destaque, banners Hero e rodapé institucional.
  - **On Primary (`#FFFFFF`):** Texto sobre botões primários e áreas escuras.
  - **Secondary (`#885031` - Caramelo Queimado / Toffee):** Subtítulos, marcadores de categoria, links ativos, bordas em foco e destaques secundários.
  - **Secondary Container (`#FFB68F`):** Fundo de badges secundários e estados selecionados na navegação (`#7A4526` para texto `On Secondary Container`).
  - **Tertiary / Accent (`#C87A3E` - Cobre Imperial / Âmbar):** Selos de Pontuação SCA (85+), CTAs de conversão ("Finalizar Compra", "Novo Café"), indicadores de filtro ativo e destaques visuais.
  - **Background Principal (`#FFF8F6` - Creme Aveia / Alabastro Quente):** Fundo geral das telas (substitui o branco puro para conforto visual).
  - **Surface / Cards (`#FFFDF9` / `#FFFFFF` - Seda Quente):** Superfície elevada para `mat-card`, tabelas (`mat-table`), modais (`mat-dialog`) e gavetas (`mat-drawer`).
  - **Surface Container (`#FFE9E2` a `#F5DED5`):** Cabeçalhos de tabelas, barras de filtro, áreas de resumo de pedido e cards secundários.
  - **Text / On Surface (`#251914` - Grão Escuro) & On Surface Variant (`#4F4541` - Canela Seca):** Textos primários e metadados técnicos.
  - **Outline & Dividers (`#817570` e `#D2C3BE` - Taupe Aveludado):** Bordas de inputs (`mat-form-field` outline) e linhas divisórias sutis de 1px.
  - **Error (`#BA1A1A`) / Error Container (`#FFDAD6`):** Mensagens de validação (`mat-error`), status `CANCELADO`, alertas de estoque insuficiente e botões de exclusão.
- **Geometria e Elevação:**
  - Inputs, botões e células: `border-radius: 8px`.
  - Cards, painéis e tabelas: `border-radius: 16px` com borda fina `1px solid #D2C3BE` e sombra quente difusa (`0 4px 16px -4px rgba(44, 30, 24, 0.06)`).
  - Chips e Badges (`mat-chip`): Formato pílula (`border-radius: 9999px`).

---

### 2. ARQUITETURA DE NAVEGAÇÃO (TOPBAR, SIDEBAR E LAYOUT)

O sistema possui dois contextos integrados que respeitam o enum `Perfil` (`ADMINISTRADOR` e `CLIENTE`):

1. **Estrutura Administrativa (Back-Office - Perfil `ADMINISTRADOR`):**
   - **Sidebar Fixa Esquerda (`mat-sidenav` em `#2C1E18`, largura 260px):**
     - Topo: Logotipo **Dorana COFFEE SHOP** em `#FFF8F6` e `#C87A3E`.
     - Grupos de Menu (`mat-nav-list`) com ícones Material Symbols:
       - *Visão Geral:* Dashboard.
       - *Catálogo & Torrefação:* Cafés, Estoque, Marcas, Categorias do Café, Regiões de Origem.
       - *Comercial:* Pedidos e Pagamentos.
       - *Acessos & Localidades:* Clientes, Administradores, Estados, Municípios.
     - Rodapé da Sidebar: Card compacto do Administrador logado (`nome`, `email`, `cargo`) e botão de Logout.
   - **Topbar Superior (`mat-toolbar` em `#FFFDF9`, altura 64px, borda inferior `#D2C3BE`):**
     - Breadcrumb dinâmico (ex.: *Catálogo > Cafés > Editar Café*), busca rápida global, indicador de perfil `ADMINISTRADOR` e botão para alternar/visualizar a Loja Pública.

2. **Estrutura da Loja / Área do Cliente (E-Commerce - Público e Perfil `CLIENTE`):**
   - **Topbar Editorial (`mat-toolbar` em `#FFFDF9`):**
     - Esquerda: Links de navegação (*Catálogo de Cafés*, *Origens*, *Marcas*, *Categorias*).
     - Centro: Logotipo **Dorana COFFEE SHOP**.
     - Direita: Campo de busca expansível (`?nome=`), menu da conta do Cliente (`Meus Pedidos`, `Meu Perfil e Endereços`, `Entrar / Cadastrar`) e ícone do Carrinho (`mat-icon` com `mat-badge` em `#C87A3E` indicando o total de itens).

---

### 3. ESPECIFICAÇÃO DETALHADA DAS TELAS E MÓDULOS (BASEADO ESTRITAMENTE NO UML)

Não adicione campos ou entidades fora do modelo de domínio. Todas as telas devem refletir exatamente os atributos e relacionamentos abaixo:

#### TELA 1: Dashboard Administrativo
- **KPIs no Topo (4 Cards em `#FFFDF9` com acento `#C87A3E`):**
  1. *Total de Pedidos* (contagem geral e divisão por `PedidoStatus`).
  2. *Receita Confirmada* (soma de `totalPedido` com `Pagamento.confirmado = true`).
  3. *Cafés Ativos no Catálogo* (contagem de `Cafe` onde `ativo = true` vs. inativos).
  4. *Alerta de Estoque Crítico* (quantidade de registros em `Estoque` com `quantidade <= 5`).
- **Seção Central (Grid 2 Colunas):**
  - *Esquerda (8 colunas):* Tabela `Últimos Pedidos Recebidos` (`id`, `dataHora`, `cliente.nome`, `totalPedido`, chip de `PedidoStatus`, status de `Pagamento` e ação "Ver Detalhes").
  - *Direita (4 colunas):* Card `Posição de Estoque Recente` listando `cafe.nome`, `estoque.quantidade`, `estoque.dataUltimaMovimentacao` e botão rápido "Ajustar Estoque".

#### TELA 2: Gestão de Cafés — Listagem e Formulário (`Cafe`)
- **Tela de Listagem (`CafeList`):**
  - **Barra de Filtros (`#FFE9E2`):** Busca textual por `nome` (`GET /cafes?nome=`), filtro de faixa de `pontuacaoSCA` (mínimo e máximo), seletores (`mat-select`) para `Marca`, `CategoriaDoCafe`, `NivelDeTorra`, `Tratamento` e toggle `Apenas Ativos`.
  - **Tabela (`mat-table` + `mat-paginator` com opções `[5, 10, 20]`):**
    - Colunas: `ID`, `Nome do Café`, `Marca` (`marca.nome`), `Categoria` (`categoriaDoCafe.nome`), `Região` (`regiaoDeOrigem.nome` - `estado.sigla`), `Torra` (badge `CLARA`, `MEDIA`, `ESCURA`), `Tratamento` (`NATURAL`, `LAVADO`, `CD`, `HONEY`), `Pontuação SCA` (badge destaque em `#2C1E18` e `#C87A3E`, ex: `87 pts`), `Peso` (`pesoGramas` g), `Preço` (`R$`), `Estoque` (`estoque.quantidade`), `Status` (`ativo`: Ativo/Inativo) e `Ações` (Editar, Ativar/Inativar).
- **Tela de Cadastro e Edição (`CafeForm`):**
  - Layout em `mat-card` dividido em seções claras:
    1. *Identificação e Classificação:* `nome` (input texto obrigatório), `descricao` (textarea até 500 caracteres), `marca` (`mat-select` obrigatório), `categoriaDoCafe` (`mat-select` obrigatório), `regiaoDeOrigem` (`mat-select` opcional exibindo região + UF) e `ativo` (`mat-slide-toggle`).
    2. *Comercial e Físico:* `preco` (input numérico monetário `R$`, obrigatório > 0), `pesoGramas` (input numérico em gramas, ex.: 250g, 500g, 1000g) e `estoqueInicial` / `quantidade` (inteiro >= 0).
    3. *Perfil de Torra, Pós-Colheita e Qualidade:* `pontuacaoSCA` (input numérico inteiro, ex.: 80 a 100), `nivelDeTorra` (`mat-ButtonToggleGroup` ou `mat-select`: `CLARA`, `MEDIA`, `ESCURA`), `tratamento` (`mat-select`: `NATURAL`, `LAVADO`, `CD`, `HONEY`).
    4. *Moagens e Notas Sensoriais (Coleções Set):*
       - `moagensPermitidas`: Seleção múltipla (`mat-selection-list` ou chips selecionáveis) com os valores do enum `Moagem`: `EM_GRAOS`, `MOIDO_FILTRO`, `MOIDO_ESPRESSO`, `MOIDO_PRENSA_FRANCESA`.
       - `notasSensoriais`: `mat-chip-listbox` múltiplo com os valores do enum `NotaSensorial`: `FRUTADO`, `FLORAL`, `CHOCOLATE`, `CARAMELO`, `CITRICO`, `ESPECIARIAS`.
  - Rodapé do Card: Botões `Cancelar` (`mat-stroked-button`), `Excluir/Inativar` (`mat-button` em `#BA1A1A`, visível apenas no modo edição) e `Salvar Café` (`mat-flat-button` em `#130804`).

#### TELA 3: Controle de Estoque (`Estoque`)
- **Listagem e Ajuste:**
  - Exibe `id`, `cafe.nome`, `cafe.marca.nome`, `quantidade` disponível, `dataUltimaMovimentacao` (formato `dd/MM/yyyy HH:mm`) e `versao` (indicador de controle otimista `@Version`).
  - Modal ou linha editável para atualizar `quantidade`, prevenindo saldo negativo e exibindo feedback caso ocorra conflito de concorrência (`HTTP 409`).

#### TELA 4: Cadastros Auxiliares do Catálogo e Localidades (CRUDs Padronizados)
Seguindo o mesmo molde visual (Toolbar com título + botão `+ Novo`, campo de filtro por `nome`, `mat-table` e `mat-paginator`, e `Form` em `mat-card`):
1. **Marcas (`Marca`):** Campos `id`, `nome` (obrigatório, máx 100), `descricao` (máx 500).
2. **Categorias do Café (`CategoriaDoCafe`):** Campos `id`, `nome` (obrigatório, máx 100), `descricao` (máx 500).
3. **Regiões de Origem (`RegiaoDeOrigem`):** Campos `id`, `nome` (ex.: Cerrado Mineiro, Sul de Minas), `descricao` (características do terroir) e vínculo obrigatório com `Estado` (`mat-select` listando Estados).
4. **Estados (`Estado`):** Campos `id`, `nome`, `sigla` (2 caracteres, ex.: MG, SP, TO) e `regiao` (`mat-select` com enum `Regiao`: `CENTRO_OESTE`, `NORDESTE`, `NORTE`, `SUDESTE`, `SUL`).
5. **Municípios (`Municipio`):** Campos `id`, `nome` e `estado` (`mat-select` de `Estado`).

#### TELA 5: Gestão de Usuários — Clientes e Administradores (`Usuario` -> `Cliente` | `Administrador`)
- **Listagem e Criação de Administradores (`/administradores`):**
  - Campos herdados de `Usuario`: `nome`, `email` (único, formato e-mail), `cpf` (11 dígitos), `login`, `senha` (apenas criação), `dataNascimento` (`mat-datepicker`) + atributo específico `cargo` (texto, máx 50, ex.: "Mestre de Torra", "Gerente Comercial") e `perfil = ADMINISTRADOR`.
- **Listagem e Cadastro/Edição de Clientes (`/clientes` e `/usuarios`):**
  - Dados principais (`Usuario`): `nome`, `email`, `cpf`, `login`, `senha`, `dataNascimento`, `perfil = CLIENTE`.
  - **Sub-coleção Dinâmica de Telefones (`FormArray` de `Telefone`):** Lista com botão `+ Adicionar Telefone`, contendo `ddd` (3 dígitos) e `numero` (até 10 dígitos) e botão de remover item. (Regra UML: Cliente possui `1..*` Telefones com `orphanRemoval`).
  - **Sub-coleção Dinâmica de Endereços (`FormArray` de `Endereco`):** Cards internos com botão `+ Adicionar Endereço`, contendo `cep` (8 dígitos), `logradouro` / `rua`, `numero`, `complemento`, `bairro` e seletor de `municipio` (`Municipio` com `Estado.sigla`).
- **Modal/Tela de Alteração de Senha (`/usuarios/me/senha`):** Campos `senhaAtual` e `novaSenha`.

#### TELA 6: Vitrine Pública, Detalhe do Café, Carrinho e Checkout (Fluxo de Compra do `Cliente`)
- **Catálogo Vitrine:**
  - Sidebar esquerda com filtros de `CategoriaDoCafe`, `Marca`, `RegiaoDeOrigem`, `NivelDeTorra`, `Tratamento`, `NotaSensorial` e faixa de `pontuacaoSCA`.
  - Grid de Cards de Café (3 por linha no desktop): Imagem da embalagem, selo `pontuacaoSCA` em Cobre (`#C87A3E`), `nome` em *Playfair Display*, `marca.nome`, `regiaoDeOrigem.nome`, chips de `notasSensoriais`, `pesoGramas` e `preco`. Se `estoque.quantidade == 0`, exibir selo `Esgotado` e desabilitar compra.
- **Página de Detalhe do Café:**
  - Apresenta todos os atributos do `Cafe`, ficha sensorial completa e seletor obrigatório de `moagemEscolhida` (restrito apenas às `moagensPermitidas` daquele café: `EM_GRAOS`, `MOIDO_FILTRO`, `MOIDO_ESPRESSO`, `MOIDO_PRENSA_FRANCESA`), seletor de `quantidade` (limitado por `estoque.quantidade`) e botão `Adicionar ao Carrinho`.
- **Carrinho e Checkout (`Pedido`, `ItemPedido`, `Pagamento`):**
  - Lista de `ItemPedido`: exibe `cafe.nome`, `moagemEscolhida`, `precoUnitarioSnapshot`, `quantidade`, `desconto` e `subTotal`.
  - Seleção de `enderecoEntrega` (escolhido dentre os `Endereco` cadastrados do `Cliente` logado).
  - Resumo financeiro do `Pedido`: `totalItens` + `valorFrete` = `totalPedido`.
  - **Seleção de Forma de Pagamento (Herança `Pagamento`):**
    - Aba 1 — **Cartão (`PagamentoCartao`):** Inputs `nomeTitular`, `numeroCartao`, `bandeira`, `validade`, `cvv` (exibindo `numeroCartaoMascarado` após confirmação).
    - Aba 2 — **PIX (`PagamentoPix`):** Exibe QR Code, `chavePixDestino` (código Copia e Cola) e contador de `dataExpiracaoToken`.
    - Aba 3 — **Boleto (`PagamentoBoleto`):** Exibe `codigoBarras` copiável e `dataVencimento`.

#### TELA 7: Gestão e Histórico de Pedidos (`Pedido`)
- **Visão do Cliente (`/meus-pedidos`) e Visão Admin (`/pedidos`):**
  - Tabela e tela de detalhes exibindo `id`, `dataHora`, dados do `cliente`, `enderecoEntrega` formatado, tabela de `itens` (`ItemPedido` com `precoUnitarioSnapshot` e `moagemEscolhida`), valores (`totalItens`, `valorFrete`, `totalPedido`) e dados do `Pagamento` (`valor`, `confirmado`, `dataConfirmacao` e tipo Cartão/Pix/Boleto).
  - **Stepper / Badges de Status (`PedidoStatus`):**
    - `AGUARDANDO_PAGAMENTO` (Âmbar `#C87A3E`)
    - `PAGO` (Verde `#2E7D32`)
    - `SEPARACAO` (Caramelo `#885031`)
    - `ENVIADO` (Azul Petróleo `#0277BD`)
    - `ENTREGUE` (Verde Escuro `#1B5E20`)
    - `CANCELADO` (Vermelho Erro `#BA1A1A`)
  - **Ação de Cancelamento (`POST /pedidos/{id}/cancelamento`):** Botão `Cancelar Pedido` com diálogo de confirmação (`mat-dialog`), informando que a ação estornará automaticamente as quantidades para o `Estoque`.

---

### 4. REGRAS DE NEGÓCIO, ESTADOS DE INTERFACE E FEEDBACK VISUAL

1. **Validações de Formulário:** Todos os campos obrigatórios devem apresentar asterisco e mensagem de erro clara (`mat-error` em `#BA1A1A`) abaixo do input quando tocados e inválidos (ex.: CPF de 11 dígitos, CEP de 8 dígitos, e-mail válido, pontuação SCA e preço positivos).
2. **Soft Delete de Café:** Quando um café já possui vínculo com `ItemPedido`, sua exclusão ou desativação alterna o atributo `ativo = false`, mantendo a integridade histórica dos pedidos antigos.
3. **Snapshot de Preço:** Na tela de detalhes do pedido, deixar claro que o valor exibido em cada item é o `precoUnitarioSnapshot` praticado no momento da compra.
4. **Feedback e Diálogos:**
   - Notificações de sucesso/erro no topo central via `mat-snack-bar` (fundo `#2C1E18` com texto `#FFF8F6` para sucesso; fundo `#BA1A1A` para erro).
   - Diálogo de confirmação (`mat-dialog`) obrigatório antes de excluir qualquer registro ou cancelar um pedido.
   - Estados de carregamento com `mat-progress-bar` em `#C87A3E` no topo das tabelas e skeletons suaves nos cards.
   - Estado vazio (Empty State) elegante com ícone em traço fino `#817570`, mensagem orientativa e botão de ação primária quando uma busca ou lista não retornar registros.
```
