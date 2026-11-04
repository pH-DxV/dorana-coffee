# 07 - Relatório de Diagnóstico e Consolidação da Auditoria

**Data da Auditoria:** 20/10/2026  
**Auditor Principal:** Antigravity (Engenheiro de Software Sênior & Orquestrador)  
**Projeto:** `lgc-api-tp1` (Backend Quarkus para E-commerce de Grãos de Café Especial)  
**Etapa:** Parte A — Diagnóstico Completo Concluído  
**Status:** ⛔ **PARADO NO PORTÃO DE APROVAÇÃO** (Aguardando Validação do Usuário)  

---

## 1. Resumo Executivo (Máx. 15 Linhas)

A API Java/Quarkus 3.26 compila e sobe com sucesso conectada ao PostgreSQL 17 local, executando o `import.sql`. No entanto, o diagnóstico técnico aprofundado revelou que o projeto encontra-se em estado frágil e vulnerável:
1. **Segurança Crítica:** O controller `UsuarioResource` é totalmente desprotegido, permitindo vazamento anônimo de CPFs e endereços, criação anônima de Administradores e escalada de privilégios via `PUT /usuarios/{id}`. Em contraste, `ClienteResource` possui proteções de dono e admin que são facilmente contornadas via `/usuarios`.
2. **Suíte de Testes Nula:** 12 dos 13 arquivos de teste estavam 100% comentados e inoperantes; o único teste ativo testa apenas um GET raso.
3. **Persistência e Concorrência:** O controle de estoque não possui trava de concorrência (`@Version`), sofre de N+1 queries na listagem de cafés e a baixa de estoque é imediata na criação do pedido, sem rotinas de estorno ou cancelamento.
4. **Violações REST Graves:** O login com erro retorna `204 No Content` em vez de `401`; buscas utilizam verbos nos paths (`/search/`, `/find/`); recursos essenciais operam no singular (`/endereco`); e `ExceptionMappers` deixam requisições com erro retornando corpo vazio ou `500` não tratado.

---

## 2. O que Funciona Bem e Deve Ser Preservado

Nem tudo na aplicação está incorreto; diversas decisões de arquitetura foram acertadas e devem ser mantidas na refatoração:

1. **Uso de `BigDecimal` para Valores Monetários:** Preços em `Cafe`, `ItemPedido`, `Pedido`, `Frete` e `Pagamento` utilizam rigorosamente `BigDecimal` com escala 2, evitando erros de ponto flutuante.
2. **Snapshot de Preço Unitário no Pedido:** O `ItemPedido` copia o preço unitário do café no instante da compra (`PedidoServiceImpl:149`), garantindo integridade contábil e histórica.
3. **Cálculo de Frete Integrado:** O `FreteService` possui regras de negócio claras baseadas na localização geográfica (UF/Região) e é executado na composição do total do pedido.
4. **Formatos de Pagamento Estruturados:** O uso de herança polimórfica para pagamentos (`PagamentoCartao`, `PagamentoPix`, `PagamentoBoleto`) com dados mascarados no cartão (`**** **** **** 1234`) é uma boa base conceitual.
5. **Adoção Inicial do Padrão RFC 7807 (Problem Details):** O modelo `Problem.java` e o `ConstraintViolationExceptionMapper` retornam erros de validação ricos e legíveis para o frontend quando ativados.
6. **Controle Fino de Acesso em `ClienteServiceImpl` e `SecurityServiceImpl`:** Os métodos de validação de posse (`validarPermissao`) foram bem concebidos para impedir acesso indevido (BOLA/IDOR), precisando apenas ser unificados e estendidos para toda a API.

---

## 3. Matriz de Achados Priorizados por Severidade

### 3.1 Achados de Severidade CRÍTICA

```
[CRÍTICA] [CONFIRMADO] Ausência total de autenticação e autorização em UsuarioResource
Onde: UsuarioResource.java (br/unitins/topicos1/lgc/Usuario/resource/UsuarioResource.java:25-85)
Comportamento atual: Todos os endpoints (/usuarios, /usuarios/{id}, /usuarios/find/{nome}, POST, PUT, DELETE) não possuem nenhuma anotação de segurança (@RolesAllowed ou @Authenticated). Testado na prática: chamada GET /usuarios sem token retornou HTTP 200 OK contendo dados confidenciais de todos os clientes e administradores (CPFs, endereços completos, telefones e perfis).
Comportamento desejado: O recurso deve ser estritamente protegido. Clientes comuns não devem acessar /usuarios; operações administrativas devem exigir role "Administrador".
Causa provável: As anotações de segurança foram comentadas ou não foram implementadas durante a evolução inicial da classe.
Impacto: Violação crítica da LGPD, vazamento de PII (Personally Identifiable Information) de toda a base de usuários.
Correção proposta: Proteger endpoints com @RolesAllowed({"Administrador"}) e redirecionar cadastro/consulta de clientes para a entidade/resource especializada Cliente.
Esforço: M
```

```
[CRÍTICA] [CONFIRMADO] Escalada de privilégio direta e criação não autorizada de Administradores
Onde: UsuarioResource.incluir:61, UsuarioResource.alterar:70 e UsuarioServiceImpl:59,117
Comportamento atual: Os endpoints POST e PUT em /usuarios aceitam o campo idPerfil no JSON sem validar permissões do requisitante. O serviço executa entity.setPerfis(Set.of(Perfil.valueOf(dto.idPerfil().longValue()))). Qualquer usuário anônimo pode enviar idPerfil: 1 e cadastrar ou promover a si mesmo para Administrador.
Comportamento desejado: O perfil deve derivar do tipo concreto da entidade (Cliente vs Administrador) ou ser atribuível apenas por um Administrador autenticado através de um fluxo fechado. O DTO de criação pública de cliente não pode permitir a escolha do perfil.
Causa provável: Reuso indiscriminado de um DTO administrativo genérico (UsuarioDTO) para operações públicas e privadas.
Impacto: Comprometimento total da segurança da aplicação.
Correção proposta: Remover o campo idPerfil do DTO público; criar endpoints separados /clientes (perfil USER forçado) e /administradores (acessível apenas por Administrador existente).
Esforço: M
```

```
[CRÍTICA] [CONFIRMADO] Login com credenciais inválidas retorna HTTP 204 No Content
Onde: AuthResource.login (br/unitins/topicos1/lgc/Auth/resource/AuthResource.java:45-48)
Comportamento atual: Quando a busca por login e hash de senha retorna nulo, o código executa explicitamente: return Response.status(Status.NO_CONTENT).build(). Testado na prática: requisição com senha errada devolve status HTTP 204.
Comportamento desejado: Falha de autenticação deve obrigatoriamente retornar HTTP 401 Unauthorized, acompanhado de header WWW-Authenticate e corpo Problem Details explicativo.
Causa provável: O desenvolvedor confundiu a ausência de registro retornado pelo repositório com o status 204, conforme comentário no próprio código ("// Ou Status.UNAUTHORIZED (401) se preferir ser mais explícito").
Impacto: Quebra frontal dos padrões HTTP e REST; dificulta o tratamento de erros no Angular, que interpreta 204 como sucesso sem conteúdo.
Correção proposta: Alterar retorno para Response.status(Status.UNAUTHORIZED).type("application/problem+json").entity(problem).build().
Esforço: P
```

```
[CRÍTICA] [CONFIRMADO] Ausência de cobertura e suite de testes 100% inoperante
Onde: src/test/java/br/unitins/topicos1/lgc/ (Todos os 13 arquivos de teste)
Comportamento atual: Dos 13 arquivos de teste existentes, 12 estão integralmente comentados com //; o único método ativo (EstadoResourceTest.testUp) apenas executa um GET /estados esperando 200. Os testes antigos dependiam de uma classe inexistente (AuthTestBase).
Comportamento desejado: Suíte de testes automatizados completa com REST Assured e @QuarkusTest cobrindo cenários de sucesso, validação, conflito, 404, 401 e 403 para todos os endpoints.
Causa provável: Mudanças graduais nos DTOs e segurança quebraram os testes antigos, levando o autor a comentar os arquivos em vez de corrigi-los.
Impacto: Regressões silenciosas, ausência total de segurança contra quebra de contratos.
Correção proposta: Descartar a suíte legada e construir do zero uma nova suíte na Fase 6 com PostgreSQL via Dev Services/Testcontainers e Test Data Builders isolados.
Esforço: G
```

---

### 3.2 Achados de Severidade ALTA

```
[ALTA] [CONFIRMADO] Falta de validação @Valid em UsuarioResource gerando HTTP 500
Onde: UsuarioResource.java:61 e 70
Comportamento atual: Os métodos incluir(UsuarioDTO dto) e alterar(Long id, UsuarioDTO dto) não possuem a anotação @Valid. Ao enviar um JSON vazio {}, o Bean Validation não atua, o código chama hashService.getHashSenha(dto.senha()) e estoura java.lang.NullPointerException gerando HTTP 500 no cliente.
Comportamento desejado: O controller deve interceptar requisições inválidas na borda via @Valid e retornar HTTP 400 Bad Request com a lista detalhada de campos faltantes.
Causa provável: Esquecimento da anotação @Valid nos parâmetros dos métodos do Resource.
Impacto: Erro 500 exposto ao cliente em dados que deveriam ser tratados como 400.
Correção proposta: Adicionar @Valid em todos os endpoints de entrada e mapear ConstraintViolationException.
Esforço: P
```

```
[ALTA] [CONFIRMADO] Vulnerabilidade N+1 Queries na listagem de Cafés
Onde: CafeServiceImpl.java:152-157 e converterParaDTO:40-44
Comportamento atual: Para cada café retornado por cafeRepository.listAll(), o service executa individualmente estoqueService.consultarQuantidade(cafe.getId()), disparando uma query SQL adicional por item na tabela estoque.
Comportamento desejado: A consulta deve carregar o saldo de estoque e as associações em uma única query com JOIN FETCH ou projeção DTO no repositório.
Causa provável: Separação física da entidade Estoque sem modelagem de consulta otimizada agregada.
Impacto: Degradação severa de tempo de resposta e sobrecarga no PostgreSQL à medida que o catálogo cresce.
Correção proposta: Criar método específico no CafeRepository com JOIN FETCH ou query nativa projetada para CafeDTOResponse.
Esforço: M
```

```
[ALTA] [CONFIRMADO] Ausência de trava de concorrência e risco de Lost Update no Estoque
Onde: Estoque.java (br/unitins/topicos1/lgc/Estoque/model/Estoque.java) e EstoqueServiceImpl.java:80-111
Comportamento atual: A entidade Estoque não possui o campo @Version para controle de concorrência otimista (nem uso de lock pessimista). Duas requisições simultâneas de compra leem o mesmo saldo e ambas decrementam, vendendo mercadoria sem saldo real.
Comportamento desejado: Controle de concorrência seguro via @Version (Optimistic Locking) lançando OptimisticLockException para retentativa segura.
Causa provável: Entidade modelada simplificadamente sem considerar tráfego concorrente de e-commerce.
Impacto: Venda de produtos com estoque negativo (overselling) e inconsistência financeira.
Correção proposta: Adicionar @Version private Long versao na entidade Estoque e tratar a concorrência no serviço de pedidos.
Esforço: M
```

```
[ALTA] [CONFIRMADO] Baixa imediata de estoque sem rotina de cancelamento ou estorno
Onde: PedidoServiceImpl.java:145 (create)
Comportamento atual: Ao criar o pedido em POST /pedidos com status AGUARDANDO_PAGAMENTO, o estoque do café é decrementado imediatamente. Não existe endpoint para cancelar pedido, nem rotina de expiração de pagamentos Pix/Boleto, nem estorno do estoque baixado.
Comportamento desejado: O pedido deve ter ciclo de vida explícito com transição para CANCELADO que devolve as quantidades ao estoque.
Causa provável: O fluxo de checkout foi implementado apenas para o caminho feliz de cartão aprovado imediatamente.
Impacto: Estoque "evapora" do sistema se o cliente gerar um pedido e desistir da compra.
Correção proposta: Implementar endpoint POST /pedidos/{id}/cancelamento com estorno transacional do estoque.
Esforço: M
```

```
[ALTA] [CONFIRMADO] Operações de escrita públicas em catálogo por anotações comentadas
Onde: CafeResource.java:36,45,54 e CategoriaDoCafeResource.java:34,42,50
Comportamento atual: As anotações @RolesAllowed("Administrador") foram comentadas com // em ambos os controllers. Qualquer cliente anônimo pode criar, editar preço e apagar cafés e categorias.
Comportamento desejado: Apenas administradores autenticados podem cadastrar, alterar e apagar produtos e categorias.
Causa provável: As anotações foram desativadas temporariamente durante o desenvolvimento para facilitar testes manuais e nunca foram reativadas.
Impacto: Qualquer pessoa na internet pode vandalizar ou apagar o catálogo do e-commerce.
Correção proposta: Reativar e testar as anotações @RolesAllowed({"Administrador"}).
Esforço: P
```

---

### 3.3 Achados de Severidade MÉDIA

```
[MÉDIA] [CONFIRMADO] Caos de padronização nas rotas de busca (/search/ vs /find/ vs /search/nome/)
Onde: CafeResource, CategoriaDoCafeResource, MarcaResource, EstadoResource, MunicipioResource, UsuarioResource, EnderecoResource
Comportamento atual: Há 4 convenções diferentes de URLs para filtragem. Ex.: /cafes/search/nome/{nome}, /marcas/search/{nome}, /estados/find/{nome}, /endereco/find/cep/{cep}.
Comportamento desejado: Padronização RESTful via query parameters nos endpoints de coleção raiz (ex.: GET /cafes?nome=...; GET /estados?nome=...).
Causa provável: Falta de guia de estilo de arquitetura REST; desenvolvimento incremental por diferentes exemplos.
Impacto: Quebra de consistência, impossibilidade de buscas combinadas e rota inadequada para proxies HTTP.
Correção proposta: Unificar todas as buscas em query parameters com paginação e ordenação.
Esforço: M
```

```
[MÉDIA] [CONFIRMADO] Recurso no singular (/endereco) em desacordo com os demais
Onde: EnderecoResource.java:25 (@Path("/endereco"))
Comportamento atual: O recurso está configurado no singular /endereco, enquanto todos os outros resources estão no plural (/estados, /municipios, /cafes, /usuarios).
Comportamento desejado: Seguir padrão REST no plural: /enderecos (ou preferencialmente como sub-recurso /clientes/me/enderecos).
Causa provável: Inconsistência de digitação inicial.
Impacto: Desvio de convenção RESTful.
Correção proposta: Ajustar @Path para "/enderecos".
Esforço: P
```

```
[MÉDIA] [CONFIRMADO] ExceptionMappers incompletos retornando corpos vazios em erros 400/404
Onde: Exception/ (UncaughtExceptionMapper, Problem.java)
Comportamento atual: Não há mappers para WebApplicationException (NotFoundException, BadRequestException, ForbiddenException). Ao chamar GET /cafes/9999, a API devolve HTTP 404 com corpo totalmente vazio (content-length: 0).
Comportamento desejado: Toda resposta de erro da API (4xx e 5xx) deve obrigatoriamente retornar JSON padronizado RFC 7807 Problem Details.
Causa provável: Mappers criados apenas para exceções de Bean Validation.
Impacto: Frontend Angular não consegue exibir mensagens amigáveis de erro aos usuários.
Correção proposta: Criar ExceptionMapper global abrangente para WebApplicationException e PSQLException.
Esforço: M
```

```
[MÉDIA] [CONFIRMADO] Exclusão física de café com histórico gera erro 500 no PostgreSQL
Onde: CafeServiceImpl.delete:128-142
Comportamento atual: Ao tentar deletar um café que já faz parte de itens de pedidos anteriores, o Hibernate tenta apagar o registro na tabela cafe e o PostgreSQL aborta com violação de chave estrangeira em itempedido, retornando HTTP 500.
Comportamento desejado: O sistema deve adotar soft delete (ativo = false) ou impedir a exclusão com mensagem clara HTTP 409 Conflict.
Causa provável: Exclusão física ingênua sem verificar restrições de integridade do histórico comercial.
Impacto: Erro 500 inesperado no cliente; risco de perda de integridade caso alguém force remoção de itens de pedidos.
Correção proposta: Adicionar campo booleano ativo na entidade Cafe e desativar o produto em vez de deletar fisicamente.
Esforço: M
```

```
[MÉDIA] [CONFIRMADO] Algoritmo de hash de senha fraco (512 iterações e salt fixo)
Onde: HashServiceImpl.java:21-27
Comportamento atual: Utiliza PBKDF2WithHmacSHA256 com apenas 512 iterações e uma constante fixa de salt ("#!blah*blah*blah!#").
Comportamento desejado: Hashing seguro de senhas com algoritmo moderno (BCrypt ou Argon2) com salt dinâmico por usuário e fator de custo elevado.
Causa provável: Implementação acadêmica simplificada.
Impacto: Vulnerabilidade contra ataques de dicionário e tabelas rainbow caso o banco de dados seja exposto.
Correção proposta: Migrar para biblioteca padrão de hash seguro (ex.: Bcrypt).
Esforço: P
```

---

### 3.4 Achados de Severidade BAIXA

```
[BAIXA] [CONFIRMADO] Entidade órfã RegiaoDeOrigem e tabelas residuais no PostgreSQL
Onde: RegiaoDeOrigem.java e schema PostgreSQL (fornecedor, produto, sabor, categoria)
Comportamento atual: A classe RegiaoDeOrigem existe como @Entity mas não possui DTOs nem CRUD; no PostgreSQL existem 5 tabelas fantasmas de exercícios anteriores que não foram dropadas.
Comportamento desejado: Código e schema limpos, sem tabelas órfãs ou classes zumbis.
Causa provável: Código e tabelas deixados para trás de versões anteriores.
Impacto: Poluição do schema e confusão na manutenção.
Correção proposta: Limpar tabelas fantasmas no script de migração e decidir se RegiaoDeOrigem será utilizada ou removida.
Esforço: P
```

```
[BAIXA] [CONFIRMADO] Convenção de nomes dos pacotes em PascalCase
Onde: src/main/java/br/unitins/topicos1/lgc/* (Auth, Cafe, Cliente, etc.)
Comportamento atual: Todos os pacotes internos iniciam com letra maiúscula.
Comportamento desejado: Padrão oficial Java com pacotes estritamente em minúsculas (auth, cafe, cliente, etc.).
Causa provável: Desatenção às convenções da linguagem Java.
Impacto: Violação de estilo e estranhamento em ferramentas padrão.
Correção proposta: Renomear pacotes para lowercase durante a refatoração.
Esforço: P
```

---

## 4. Dúvidas de Domínio e de Intenção (Para Validação do Usuário)

As decisões abaixo dependem exclusivamente da sua definição de negócio antes de avançarmos para o planejamento da refatoração (Parte B):

### Dúvida 1: Escopo e Existência do Recurso `/usuarios`
- **Contexto:** Hoje existem `/usuarios` e `/clientes` competindo entre si.
- **Opções:**
  1. *(Recomendada)* **Extinguir `/usuarios` para clientes finais:** Deixar o autocadastro e a gestão de clientes estritamente em `/clientes`. Criar `/administradores` para gerenciar funcionários/administradores (restrito a admin). Manter `/usuarios/me` apenas como endpoint de perfil do usuário logado (seja cliente ou admin) para ver seus próprios dados e trocar senha.
  2. **Manter `/usuarios` como CRUD técnico exclusivo para Administrador:** Onde o admin gerencia qualquer usuário do sistema (com role controlada).
- **Sua escolha:** [ ] Opção 1  [ ] Opção 2

---

### Dúvida 2: Modelo de Identificação de Login (E-mail vs Login puro)
- **Contexto:** A entidade `Usuario` atual possui `login` (string livre) e `cpf`, mas **não possui campo de e-mail**.
- **Opções:**
  1. *(Recomendada)* **Adicionar campo `email` único e obrigatório:** O login passa a ser feito preferencialmente por e-mail e senha (padrão absoluto de e-commerce moderno), mantendo o CPF apenas para dados fiscais de cobrança/entrega.
  2. **Manter `login` textual puro e adicionar `email` apenas como dado complementar.**
- **Sua escolha:** [ ] Opção 1  [ ] Opção 2

---

### Dúvida 3: Ciclo de Vida e Baixa de Estoque do Pedido
- **Contexto:** Hoje o estoque baixa na criação do pedido (`AGUARDANDO_PAGAMENTO`). Se o cliente não pagar o Pix ou Boleto, o estoque fica retido.
- **Opções:**
  1. *(Recomendada)* **Reserva temporária com expiração:** A criação do pedido reserva o estoque; se o pagamento não for confirmado dentro do prazo (ex.: 30 min para Pix, 3 dias para Boleto) ou for cancelado, o pedido vai para `CANCELADO` e o estoque é devolvido automaticamente.
  2. **Baixar estoque somente na confirmação do pagamento (`PAGO`):** Risco de vender mais do que tem se muitos clientes fecharem pedidos simultâneos no boleto antes de pagar.
- **Sua escolha:** [ ] Opção 1  [ ] Opção 2

---

### Dúvida 4: Atributo Moagem de Café no Domínio
- **Contexto:** Em lojas de cafés especiais, os clientes escolhem se querem o café "Em Grãos" ou "Moído" (e para qual método: Prensa Francesa, Filtro V60, Espresso, Cafeteira Italiana). Hoje o sistema só tem peso, torra e tratamento.
- **Opções:**
  1. *(Recomendada)* **Adicionar `Moagem` no catálogo e no `ItemPedido`:** Permitir que o cliente selecione o tipo de moagem desejado no ato da compra.
  2. **Vender apenas em grãos inteiros:** Manter o modelo sem moagem.
- **Sua escolha:** [ ] Opção 1  [ ] Opção 2

---

### Dúvida 5: Destino da Entidade Órfã `RegiaoDeOrigem`
- **Contexto:** A classe `RegiaoDeOrigem` está sem repositório, DTO ou controller, enquanto existe o enum `Regiao` (Norte, Sul, etc.) usado para cálculo de frete e estados.
- **Opções:**
  1. *(Recomendada)* **Completar o cadastro de `RegiaoDeOrigem`:** Implementar o CRUD completo de Regiões de Origem Produtora de Café (ex.: Cerrado Mineiro, Sul de Minas, Mogiana, Mantiqueira de Minas, Alta Mogiana) vinculando formalmente ao Café no catálogo.
  2. **Remover a entidade `RegiaoDeOrigem`:** Deixar apenas a descrição textual em `Cafe.descricao` e limpar a tabela do banco.
- **Sua escolha:** [ ] Opção 1  [ ] Opção 2

---

## 5. ⛔ Portão de Aprovação (Parada Obrigatória)

A Fase 4 da Parte A está formalmente concluída. Toda a documentação e evidências foram geradas e salvas em `docs/auditoria/`:
- `01-reconhecimento.md`
- `02-dominio-e-uml-atual.md`
- `03-fichas-dos-endpoints.md`
- `04-analise-cruzada-endpoints.md`
- `05-revisao-camadas-e-seguranca.md`
- `06-analise-hierarquia-usuario.md`
- `07-relatorio-de-diagnostico.md`

> [!IMPORTANT]
> **PARADA OBRIGATÓRIA:** Em conformidade estrita com o princípio "diagnosticar antes de operar" e as instruções da Seção 6 do mandato, **nenhum código de produção foi alterado e nenhum passo da Parte B será iniciado sem a sua validação prévia**.
> Por favor, responda às 5 dúvidas de domínio acima e indique sua aprovação para avançarmos para a Fase 5 (Plano de Refatoração).
