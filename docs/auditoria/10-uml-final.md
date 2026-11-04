# 10 - Modelagem UML Final (Estado Alvo Pós-Refatoração)

**Data:** 21/10/2026  
**Arquiteto:** Antigravity  
**Fase:** Modelagem da Arquitetura Alvo (Comparativo com `02-dominio-e-uml-atual.md`)  

---

## 1. Diagrama de Classes UML Alvo (Arquitetura Refatorada)

```mermaid
classDiagram
    direction TB

    class DefaultEntity {
        <<abstract, MappedSuperclass>>
        #Long id
        +getId() Long
        +setId(Long id) void
    }

    class Usuario {
        <<abstract, JOINED>>
        #String nome
        #String email
        #String cpf
        #String senha
        #LocalDate dataNascimento
        #Perfil perfil
        +getPerfil() Perfil
    }

    class Cliente {
        <<JOINED>>
        -List~Endereco~ enderecos
        -List~Telefone~ telefones
        -List~Pedido~ pedidos
    }

    class Administrador {
        <<JOINED>>
        -String cargo
    }

    class Perfil {
        <<enumeration>>
        ADMINISTRADOR
        CLIENTE
    }

    class Telefone {
        -String ddd
        -String numero
        -Cliente cliente
    }

    class Endereco {
        -String cep
        -String logradouro
        -String numero
        -String complemento
        -String bairro
        -Municipio municipio
        -Cliente cliente
    }

    class Estado {
        -String nome
        -String sigla
        -Regiao regiao
    }

    class Municipio {
        -String nome
        -Estado estado
    }

    class Regiao {
        <<enumeration>>
        CENTRO_OESTE
        NORDESTE
        NORTE
        SUDESTE
        SUL
    }

    class RegiaoDeOrigem {
        -String nome
        -String descricao
        -Estado estado
    }

    class Cafe {
        -String nome
        -String descricao
        -BigDecimal preco
        -BigDecimal pesoGramas
        -Integer pontuacaoSCA
        -NivelDeTorra nivelDeTorra
        -Tratamento tratamento
        -Marca marca
        -CategoriaDoCafe categoriaDoCafe
        -RegiaoDeOrigem regiaoDeOrigem
        -Set~Moagem~ moagensPermitidas
        -Set~NotaSensorial~ notasSensoriais
        -Boolean ativo
    }

    class Moagem {
        <<enumeration>>
        EM_GRAOS
        MOIDO_FILTRO
        MOIDO_ESPRESSO
        MOIDO_PRENSA_FRANCESA
    }

    class Marca {
        -String nome
        -String descricao
    }

    class CategoriaDoCafe {
        -String nome
        -String descricao
    }

    class Estoque {
        -Integer quantidade
        -LocalDateTime dataUltimaMovimentacao
        -Long versao
        -Cafe cafe
    }

    class Pedido {
        -LocalDateTime dataHora
        -BigDecimal totalItens
        -BigDecimal valorFrete
        -BigDecimal totalPedido
        -PedidoStatus status
        -Cliente cliente
        -Endereco enderecoEntrega
        -List~ItemPedido~ itens
        -Pagamento pagamento
    }

    class PedidoStatus {
        <<enumeration>>
        AGUARDANDO_PAGAMENTO
        PAGO
        SEPARACAO
        ENVIADO
        ENTREGUE
        CANCELADO
    }

    class ItemPedido {
        -BigDecimal precoUnitarioSnapshot
        -Integer quantidade
        -BigDecimal desconto
        -Moagem moagemEscolhida
        -Cafe cafe
        -Pedido pedido
        +getSubTotal() BigDecimal
    }

    class Pagamento {
        <<abstract, JOINED>>
        #BigDecimal valor
        #Boolean confirmado
        #LocalDateTime dataConfirmacao
        #Pedido pedido
    }

    class PagamentoCartao {
        -String nomeTitular
        -String numeroCartaoMascarado
        -String bandeira
    }

    class PagamentoPix {
        -String chavePixDestino
        -LocalDateTime dataExpiracaoToken
    }

    class PagamentoBoleto {
        -String codigoBarras
        -LocalDate dataVencimento
    }

    DefaultEntity <|-- Usuario
    DefaultEntity <|-- Endereco
    DefaultEntity <|-- Telefone
    DefaultEntity <|-- Estado
    DefaultEntity <|-- Municipio
    DefaultEntity <|-- RegiaoDeOrigem
    DefaultEntity <|-- Cafe
    DefaultEntity <|-- Marca
    DefaultEntity <|-- CategoriaDoCafe
    DefaultEntity <|-- Estoque
    DefaultEntity <|-- Pedido
    DefaultEntity <|-- ItemPedido
    DefaultEntity <|-- Pagamento

    Usuario <|-- Cliente : InheritanceType.JOINED
    Usuario <|-- Administrador : InheritanceType.JOINED

    Pagamento <|-- PagamentoCartao : InheritanceType.JOINED
    Pagamento <|-- PagamentoPix : InheritanceType.JOINED
    Pagamento <|-- PagamentoBoleto : InheritanceType.JOINED

    Cliente "1" *-- "1..*" Endereco : cascade ALL, orphanRemoval
    Cliente "1" *-- "1..*" Telefone : cascade ALL, orphanRemoval
    Cliente "1" o-- "0..*" Pedido : cliente

    Municipio "0..*" --> "1" Estado : estado
    Endereco "0..*" --> "1" Municipio : municipio

    Cafe "0..*" --> "1" Marca : marca
    Cafe "0..*" --> "1" CategoriaDoCafe : categoriaDoCafe
    Cafe "0..*" --> "0..1" RegiaoDeOrigem : regiaoDeOrigem
    Cafe "1" --> "1" Estoque : estoque (optimistic locking)

    Pedido "1" *-- "1..*" ItemPedido : cascade ALL, orphanRemoval
    Pedido "0..*" --> "1" Endereco : enderecoEntrega
    ItemPedido "0..*" --> "1" Cafe : cafe
    Pagamento "1" --> "1" Pedido : pedido (unique)
```

---

## 2. Diagrama Entidade-Relacionamento Final (PostgreSQL)

```mermaid
erDiagram
    USUARIO {
        bigint id PK
        varchar_100 nome
        varchar_150 email UK
        varchar_11 cpf UK
        varchar_255 senha
        date datanascimento
        varchar_30 perfil
    }

    CLIENTE {
        bigint id PK, FK
    }

    ADMINISTRADOR {
        bigint id PK, FK
        varchar_50 cargo
    }

    TELEFONE {
        bigint id PK
        varchar_3 ddd
        varchar_10 numero
        bigint id_cliente FK
    }

    ENDERECO {
        bigint id PK
        varchar_8 cep
        varchar_150 logradouro
        varchar_10 numero
        varchar_100 complemento
        varchar_60 bairro
        bigint id_municipio FK
        bigint id_cliente FK
    }

    ESTADO {
        bigint id PK
        varchar_60 nome
        varchar_2 sigla
        varchar_20 regiao
    }

    MUNICIPIO {
        bigint id PK
        varchar_60 nome
        bigint id_estado FK
    }

    REGIAODEORIGEM {
        bigint id PK
        varchar_100 nome
        varchar_500 descricao
        bigint id_estado FK
    }

    MARCA {
        bigint id PK
        varchar_100 nome
        varchar_500 descricao
    }

    CATEGORIADOCAFE {
        bigint id PK
        varchar_100 nome
        varchar_500 descricao
    }

    CAFE {
        bigint id PK
        varchar_100 nome
        varchar_500 descricao
        numeric_10_2 preco
        numeric_10_2 pesogramas
        integer pontuacao_sca
        varchar_50 niveldetorra
        varchar_50 tratamento
        boolean ativo
        bigint id_marca FK
        bigint id_categoria_cafe FK
        bigint id_regiao_origem FK
    }

    CAFE_MOAGEM {
        bigint id_cafe FK
        varchar_50 moagem
    }

    CAFE_NOTASENSORIAL {
        bigint id_cafe FK
        varchar_50 nota_sensorial
    }

    ESTOQUE {
        bigint id PK
        integer quantidade
        timestamp dataultimamovimentacao
        bigint versao
        bigint id_cafe FK, UK
    }

    PEDIDO {
        bigint id PK
        timestamp datahora
        numeric_10_2 totalitens
        numeric_10_2 valorfrete
        numeric_10_2 totalpedido
        varchar_30 status
        bigint id_cliente FK
        bigint id_endereco_entrega FK
    }

    ITEMPEDIDO {
        bigint id PK
        numeric_10_2 precounitariosnapshot
        integer quantidade
        numeric_10_2 desconto
        varchar_50 moagem
        bigint id_cafe FK
        bigint id_pedido FK
    }

    PAGAMENTO {
        bigint id PK
        numeric_10_2 valor
        boolean confirmado
        timestamp dataconfirmacao
        bigint id_pedido FK, UK
    }

    PAGAMENTOCARTAO {
        bigint id PK, FK
        varchar_100 nometitular
        varchar_30 numerocartaomascarado
        varchar_30 bandeira
    }

    PAGAMENTOPIX {
        bigint id PK, FK
        varchar_255 chavepixdestino
        timestamp dataexpiracaotoken
    }

    PAGAMENTOBOLETO {
        bigint id PK, FK
        varchar_255 codigobarras
        date datavencimento
    }

    USUARIO ||--o| CLIENTE : "PK = FK (JOINED)"
    USUARIO ||--o| ADMINISTRADOR : "PK = FK (JOINED)"
    CLIENTE ||--o{ TELEFONE : "id_cliente"
    CLIENTE ||--o{ ENDERECO : "id_cliente"
    CLIENTE ||--o{ PEDIDO : "id_cliente"

    ESTADO ||--o{ MUNICIPIO : "id_estado"
    ESTADO ||--o{ REGIAODEORIGEM : "id_estado"
    MUNICIPIO ||--o{ ENDERECO : "id_municipio"
    ENDERECO ||--o{ PEDIDO : "id_endereco_entrega"

    MARCA ||--o{ CAFE : "id_marca"
    CATEGORIADOCAFE ||--o{ CAFE : "id_categoria_cafe"
    REGIAODEORIGEM ||--o{ CAFE : "id_regiao_origem"
    CAFE ||--|{ CAFE_MOAGEM : "id_cafe"
    CAFE ||--|{ CAFE_NOTASENSORIAL : "id_cafe"
    CAFE ||--|| ESTOQUE : "id_cafe"

    PEDIDO ||--|{ ITEMPEDIDO : "id_pedido"
    CAFE ||--o{ ITEMPEDIDO : "id_cafe"
    PEDIDO ||--o| PAGAMENTO : "id_pedido"

    PAGAMENTO ||--o| PAGAMENTOCARTAO : "PK = FK (JOINED)"
    PAGAMENTO ||--o| PAGAMENTOPIX : "PK = FK (JOINED)"
    PAGAMENTO ||--o| PAGAMENTOBOLETO : "PK = FK (JOINED)"
```

---

## 3. Principais Ganhos e Diferenças em Relação ao Estado Inicial

| Item | Estado Inicial (`02-dominio-e-uml-atual.md`) | Estado Final (`10-uml-final.md`) | Ganho Arquitetural |
|---|---|---|---|
| **Hierarquia de Usuários** | `Usuario` concreto com tabela associativa `usuario_perfil`. `Cliente` vazio. Sem classe `Administrador`. | `Usuario` abstrato com herança `JOINED` para `Cliente` e `Administrador`. | **Fim da escalada de privilégios.** Responsabilidades e atributos explícitos por tipo de ator. |
| **Identificação** | `login` textual puro e sem campo `email`. | `email` único e obrigatório com validação `@Email`. | Alinhamento total com padrões de e-commerce e recuperação de conta. |
| **Segurança em Pedidos** | Pedido vinculado a `Usuario` genérico. | Pedido vinculado a `Cliente`. | Coerência de domínio: apenas clientes compram e possuem histórico de pedidos. |
| **Concorrência em Estoque** | Entidade `Estoque` sem controle de versão. Risco de Lost Update. | `@Version private Long versao;` | Prevenção absoluta contra estoque negativo em concorrência. |
| **Ciclo de Vida do Pedido** | Baixa imediata de estoque sem possibilidade de cancelamento/estorno. | Endpoint `POST /pedidos/{id}/cancelamento` com estorno transacional do estoque. | Gestão correta de pedidos desistidos ou boletos/pix expirados. |
| **Atributo Moagem** | Inexistente. | Enum `Moagem` no catálogo e seleção no `ItemPedido`. | Especialização para o domínio real de grãos de café. |
| **Região de Origem** | Entidade órfã sem CRUD nem DTOs. | Entidade completa integrada a Estado e Café. | Valorização do terroir de cafés especiais (Sul de Minas, Cerrado Mineiro, etc.). |
| **Tratamento de Exclusão de Café** | Exclusão física que estourava erro 500 se o café já tivesse sido comprado. | Soft delete com campo `ativo` (booleano). | Integridade histórica garantida sem quebra de banco. |
