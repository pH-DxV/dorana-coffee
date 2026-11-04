# 01 - Reconhecimento do Projeto e Inventário Inicial

**Data da Auditoria:** 20/10/2026  
**Auditor:** Antigravity (Engenheiro de Software Sênior & Orquestrador)  
**Projeto:** `lgc-api-tp1` (E-commerce de Grãos de Café Especial)  
**Status do Portão:** Fase 0 Concluída — Somente Leitura e Observação  

---

## 1. Visão Geral do Build e Tecnologias

### 1.1 Sistema de Build e Versões
- **Ferramenta de Build:** Apache Maven 3.x com wrapper executável (`mvnw` / `mvnw.cmd`).
- **Versão do Java:** Java 21 (`<maven.compiler.release>21</maven.compiler.release>`).
- **Framework Principal:** Quarkus `3.26.3` (`quarkus-bom` importado em `io.quarkus.platform`).
- **Compilador e Testes:**
  - `maven-compiler-plugin: 3.14.0` (configurado com `<parameters>true</parameters>`)
  - `maven-surefire-plugin: 3.5.3`
  - `maven-failsafe-plugin: 3.5.3`
- **Banco de Dados:** PostgreSQL 17 (driver JDBC `quarkus-jdbc-postgresql`).
- **Frontend Consumidor:** Angular 19+ localizado no subdiretório `frontend/` (consome via REST/JSON na porta `8080`).

### 1.2 Extensões Quarkus Instaladas (`pom.xml`)
| Extensão | Finalidade Declarada | Avaliação de Uso |
|---|---|---|
| `quarkus-rest` | Motor JAX-RS / Quarkus REST reativo | Em uso ativo em todos os Resources |
| `quarkus-rest-jackson` | Serialização/Deserialização JSON via Jackson | Em uso com `@JsonFormat`, `@JsonProperty` |
| `quarkus-smallrye-openapi` | Geração de especificação OpenAPI 3.0 e Swagger UI | Ativo em `/q/openapi` e `/q/swagger-ui` |
| `quarkus-hibernate-orm-panache` | Repositories padrão Panache (`PanacheRepository<T>`) | Em uso nos 12 Repositories |
| `quarkus-hibernate-orm` | Core JPA / Hibernate 6.x | Em uso ativo com anotações JPA |
| `quarkus-jdbc-postgresql` | Driver PostgreSQL nativo | Conexão local `localhost:5432/topicos1db` |
| `quarkus-arc` | Injeção de dependências CDI (`@ApplicationScoped`, `@Inject`) | Em uso em todos os Services e Resources |
| `quarkus-hibernate-validator` | Bean Validation (JSR-380 / Hibernate Validator) | Parcialmente configurado nos DTOs, ausente em vários controllers |
| `quarkus-smallrye-jwt` | Validação de tokens JWT Bearer (`@RolesAllowed`, `JsonWebToken`) | Em uso parcial e inconsistente |
| `quarkus-smallrye-jwt-build` | Emissão de tokens JWT na autenticação | Em uso no `JwtServiceImpl` |
| `quarkus-junit5` | Execução de testes unitários e de integração `@QuarkusTest` | Presente, mas suite quase 100% desativada |
| `rest-assured` | DSL para testes de endpoints HTTP | Presente nos arquivos de teste comentados |
| `hamcrest: 2.2` | Matchers de asserção | Presente nas dependências de teste |

---

## 2. Configurações de Ambiente (`application.properties`)

### 2.1 Análise Linha a Linha das Configurações
- **Banco de Dados:**
  - `quarkus.datasource.db-kind=postgresql`
  - `quarkus.datasource.username=topicos1`
  - `quarkus.datasource.password=123456`
  - `quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/topicos1db`
  - `quarkus.devservices.enabled=false`: DevServices desabilitados forçando uso do banco local.
- **Geração de Schema DDL:**
  - `quarkus.hibernate-orm.database.generation=drop-and-create`
  > [!WARNING]
  > **Propriedade Deprecated:** No Quarkus 3+, a chave correta é `quarkus.hibernate-orm.schema-generation.database.action=drop-and-create`. Devido à depreciação, o Quarkus emite WARN no boot e não executa o drop de tabelas preexistentes que não estão mapeadas no código atual (ex.: tabelas residuais `fornecedor`, `produto`, `sabor`).
- **Logs de Banco de Dados:**
  - `quarkus.hibernate-orm.log.sql=true` e `quarkus.hibernate-orm.log.bind-parameters=true`: Essenciais para a auditoria de queries e detecção de N+1.
- **Segurança e JWT:**
  - `mp.jwt.verify.issuer=unitins-jwt`
  - `mp.jwt.verify.publickey.location=classpath:token/publicKey.pem`
  - `smallrye.jwt.sign.key.location=classpath:token/privateKey.pem`
  - `mp.jwt.verify.groups.claim=groups`
  > [!CAUTION]
  > As chaves RSA pública e privada estão salvas dentro do próprio repositório em `src/main/resources/token/`. Isso representa um risco de segurança se o repositório for compartilhado.
- **CORS:**
  - `quarkus.http.cors=true` (Deprecated, substituída por `quarkus.http.cors.enabled=true`)
  - `quarkus.http.cors.origins=http://localhost:4200`
- **Tratamento de Erros Problem Details:**
  - `%dev.problem.base-url=http://localhost:8080`
  - `%test.problem.base-url=http://localhost:8081`
  - `%prod.problem.base-url=https://api.seusistema.com`

---

## 3. Estrutura de Pacotes e Padrões de Código

### 3.1 Nomenclatura e Organização
- **Convenção de Pacotes Incomum:** Todos os pacotes internos foram criados em **PascalCase** em vez do padrão Java oficial (lowercase):
  - `br.unitins.topicos1.lgc.Auth`
  - `br.unitins.topicos1.lgc.Cafe`
  - `br.unitins.topicos1.lgc.CategoriaDoCafe`
  - `br.unitins.topicos1.lgc.Cliente`
  - `br.unitins.topicos1.lgc.DefaultEntity`
  - `br.unitins.topicos1.lgc.Endereco`
  - `br.unitins.topicos1.lgc.Estado`
  - `br.unitins.topicos1.lgc.Estoque`
  - `br.unitins.topicos1.lgc.Exception`
  - `br.unitins.topicos1.lgc.Frete`
  - `br.unitins.topicos1.lgc.Hash`
  - `br.unitins.topicos1.lgc.ItemPedido`
  - `br.unitins.topicos1.lgc.JwT`
  - `br.unitins.topicos1.lgc.Marca`
  - `br.unitins.topicos1.lgc.Municipio`
  - `br.unitins.topicos1.lgc.Pagamento`
  - `br.unitins.topicos1.lgc.Pedido`
  - `br.unitins.topicos1.lgc.Perfil`
  - `br.unitins.topicos1.lgc.Regiao`
  - `br.unitins.topicos1.lgc.RegiaoDeOrigem`
  - `br.unitins.topicos1.lgc.Security`
  - `br.unitins.topicos1.lgc.Telefone`
  - `br.unitins.topicos1.lgc.Usuario`
  - `br.unitins.topicos1.lgc.jpa.model` (único em lowercase)
- **Divisão Interna por "Módulo":** Cada pacote possui subpastas:
  - `dto/` (Records com sufixo `DTO` para entrada e `DTOResponse` para saída)
  - `model/` (Entidades JPA herdando de `DefaultEntity` ou enums)
  - `repository/` (Classes implementando `PanacheRepository<T>`)
  - `service/` (Interface + classe concreta `*ServiceImpl`)
  - `resource/` (Endpoints JAX-RS)

---

## 4. Inventário Completo de Componentes de Produção

### 4.1 Entidades JPA (`@Entity`)
| Entidade | Tabela | Herança | Observação |
|---|---|---|---|
| `Usuario` | `usuario` | `InheritanceType.JOINED` | Contém nome, login, senha hash, cpf, dataNasc, telefones, endereços, perfis. Não possui e-mail. |
| `Cliente` | `cliente` | Subclasse de `Usuario` | Sem nenhum campo próprio. Apenas chave estrangeira para `usuario(id)`. |
| `Cafe` | `cafe` | Não | Produto de café com preço, peso, torra, notas sensoriais, pontuação, marca, categoria. |
| `CategoriaDoCafe` | `categoriadocafe` | Não | Nome e descrição da categoria do grão. |
| `RegiaoDeOrigem` | `regiaodeorigem` | Não | **Órfã.** Mapeada como `@Entity`, mas sem DTO, Service nem Resource. |
| `Marca` | `marca` | Não | Marca/produtor do café. |
| `Estoque` | `estoque` | Não | Saldo de estoque vinculado a um café (`@OneToOne`). Sem controle de versão. |
| `Pedido` | `pedido` | Não | Cabeçalho do pedido vinculado a `Usuario` e `Endereco`. |
| `ItemPedido` | `itempedido` | Não | Item do pedido com preço unitário snapshot, quantidade, desconto e café. |
| `Pagamento` | `pagamento` | `InheritanceType.JOINED` | Classe abstrata de pagamento com valor, status confirmado e data. |
| `PagamentoCartao` | `pagamentocartao` | Subclasse de `Pagamento` | Dados do cartão (titular, número mascarado, bandeira). |
| `PagamentoPix` | `pagamentopix` | Subclasse de `Pagamento` | Chave Pix e expiração de token. |
| `PagamentoBoleto` | `pagamentoboleto` | Subclasse de `Pagamento` | Código de barras e vencimento. |
| `Endereco` | `endereco` | Não | Logradouro associado a um usuário e a um município. |
| `Telefone` | `telefone` | Não | DDD e número associados a um usuário. |
| `Municipio` | `municipio` | Não | Nome e relacionamento com Estado. |
| `Estado` | `estado` | Não | Nome, sigla e região (enum). |

### 4.2 Componentes Órfãos, Residuais ou Incompletos
1. **`RegiaoDeOrigem.java` (`model/`):**
   - Possui `@Entity`, `@Table`, estende `DefaultEntity`.
   - Está associada em `Cafe.java` (`@ManyToOne private RegiaoDeOrigem regiaoDeOrigem;`), porém em `CafeDTO` e `CafeDTOResponse` o campo está explicitamente comentado como `// Futuro` e `// TODO`.
   - Não existe repositório, service nem resource para gerenciar regiões de origem.
2. **`EstoqueResource.java` Inexistente:**
   - Existe `EstoqueService`, `EstoqueServiceImpl`, `EstoqueRepository` e a entidade `Estoque`.
   - Existe inclusive um arquivo de testes chamado `EstoqueResourceTest.java`.
   - **Porém não existe `EstoqueResource.java`.** O estoque só pode ser alterado como efeito colateral da criação de pedido ou do CRUD de café. Um administrador não tem rota para abastecer o estoque.
3. **Tabelas Residuais no PostgreSQL:**
   - No schema real do PostgreSQL existem as tabelas: `fornecedor`, `produto`, `produto_sabor`, `sabor`, `categoria`.
   - Nenhuma dessas tabelas possui classe correspondente no código Java atual. São sobras de versões prévias mantidas no banco porque o `drop-and-create` não limpou tabelas não gerenciadas.

---

## 5. Status de Compilação e Execução

### 5.1 Compilação do Projeto
- **Comando:** `.\mvnw.cmd compile`
- **Resultado:** **BUILD SUCCESS**
- **Tempo:** 7.31s
- **Fontes compiladas:** 125 arquivos Java no pacote `main`.

### 5.2 Compilação de Testes
- **Comando:** `.\mvnw.cmd test-compile`
- **Resultado:** **BUILD SUCCESS**
- **Tempo:** 6.48s
- **Fontes de teste compiladas:** 13 arquivos Java.

### 5.3 Execução da Aplicação (Quarkus Dev Mode)
- **Comando:** `.\mvnw.cmd quarkus:dev`
- **Resultado:** **Subiu com sucesso em 4.8s** escutando na porta `8080`.
- **Banco de Dados:** Conexão bem-sucedida com `topicos1db` no PostgreSQL 17 local.
- **Script de Inicialização:** `import.sql` executou perfeitamente populando 27 estados, 27 municípios, 2 usuários (`admin` e `cliente`), marcas, categorias, cafés, notas sensoriais, estoque, 1 pedido e 1 pagamento.

---

## 6. Inventário e Diagnóstico dos Testes Antigos

Ao executar `.\mvnw.cmd test`, apenas **1 teste** é executado (`EstadoResourceTest.testUp`). Os demais 12 arquivos de teste estão **100% comentados com `//`**.

| Arquivo de Teste | Linhas Totais | Linhas Ativas | Situação Atual | Classificação | Justificativa Técnica |
|---|---|---|---|---|---|
| `AuthResourceTest.java` | 121 | 0 | 100% comentado. | **Descartar** | Espera status `204` para login com erro (aceitando bug de contrato); acoplado à criação de cliente via endpoint; sem isolamento de fixtures. |
| `CafeResourceTest.java` | 113 | 0 | 100% comentado. | **Descartar** | Tentava herdar de `AuthTestBase` (classe inexistente no projeto); depende de ordem fixa (`@TestMethodOrder`). |
| `CategoriaDoCafeResourceTest.java` | 195 | 0 | 100% comentado. | **Descartar** | Herdava de `AuthTestBase`; acoplado a IDs globais estáticos compartilhados entre testes. |
| `ClienteResourceTest.java` | 55 | 0 | 100% comentado. | **Descartar** | Teste incompleto e não funcional; dependia de login em setup compartilhado. |
| `EnderecoResourceTest.java` | 165 | 0 | 100% comentado. | **Descartar** | Totalmente comentado; dados fixos em variáveis estáticas. |
| `EstadoResourceTest.java` | 129 | 12 | Contém apenas 1 método ativo (`testUp`) que faz `GET /estados` esperando 200. O restante do CRUD está comentado. | **Reescrever** | O teste ativo é um teste superficial (não testa POST, PUT, DELETE, validações nem erros); o bloco original comentado herda da classe fantasma `AuthTestBase`. |
| `EstoqueResourceTest.java` | 222 | 0 | 100% comentado. O teste final continha `statusCode(anyOf(is(200), is(404)))` para rota `/estoque` inexistente. | **Descartar** | Teste vazio que aceita `404` como válido para mascarar inexistência de controller; fluxo altamente acoplado a Café, Pedido e Marca. |
| `MarcaResourceTest.java` | 166 | 0 | 100% comentado. | **Descartar** | Herdava de `AuthTestBase`; acoplamento temporal via `@Order`. |
| `MunicipioResourceTest.java` | 119 | 0 | 100% comentado. | **Descartar** | Depende de IDs criados em outros testes sem isolamento. |
| `PagamentoResourceTest.java` | 40 | 0 | 100% comentado. | **Descartar** | Extremamente raso, dependia de pedido pré-criado em outro teste. |
| `PedidoResourceTest.java` | 51 | 0 | 100% comentado. | **Descartar** | Dependia de fluxo manual de múltiplos cadastros no setup sem builders. |
| `TelefoneResourceTest.java` | 154 | 0 | 100% comentado. | **Descartar** | Herdava de `AuthTestBase`; dados estáticos compartilhados. |
| `UsuarioResourceTest.java` | 241 | 0 | 100% comentado. | **Descartar** | Tentativa de testar CRUD acoplado que não isola usuário admin de cliente. |

### Conclusão da Fase 0 sobre a Suíte de Testes
A base de testes existente **não oferece nenhuma rede de segurança**. Quase 100% dos testes foram comentados e abandonados provavelmente por quebras decorrentes de alterações na autenticação e nos DTOs, e pela dependência de uma classe base ausente (`AuthTestBase`). 
Portanto, a recomendação categórica é **descartar o código de teste legado e reescrever uma suíte moderna do zero na Fase 6**, baseando-se no comportamento desejado aprovado.
