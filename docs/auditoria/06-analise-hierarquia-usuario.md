# 06 - Análise Técnica da Hierarquia Usuario -> Cliente / Administrador

**Data da Auditoria:** 20/10/2026  
**Auditor:** Arquiteto de Domínio / Revisor de Qualidade e Segurança  
**Fase:** Análise Teórica e Diagnóstico da Parte A (Execução planejada para a Parte B)  

---

## 1. Como Usuario e Cliente Estão Modelados Hoje

### 1.1 O Modelo Atual
- **Entidade `Usuario`:**
  - Anotada com `@Entity` e `@Inheritance(strategy = InheritanceType.JOINED)`.
  - Atributos: `id`, `nome`, `cpf` (UNIQUE), `login` (UNIQUE), `senha` (hash Base64), `dataNascimento`.
  - Relacionamentos: `telefones` (`@OneToMany`), `enderecos` (`@OneToMany`), `perfis` (`@ElementCollection` com tabela `usuario_perfil`).
  - **Não possui e-mail**.
- **Entidade `Cliente`:**
  - `public class Cliente extends Usuario`.
  - Anotada com `@Entity` e `@PrimaryKeyJoinColumn(name = "id")`.
  - **Está 100% vazia**. Não possui nenhum atributo específico.
  - Tabela `cliente` no PostgreSQL possui somente uma coluna: `id bigint REFERENCES usuario(id)`.
- **Entidade `Administrador`:**
  - **Inexistente.** Não há classe Java nem tabela no banco.

### 1.2 Como o Papel de Administrador é Determinado Hoje
Hoje, ser Administrador é apenas um **efeito colateral acidental** de possuir um registro na tabela associativa `usuario_perfil` com `id_perfil = 1` (`Perfil.ADM`):
```sql
INSERT INTO usuario_perfil (id_usuario, id_perfil) VALUES (1, 1);
```
No momento do login (`AuthResource.login`), o serviço `JwtServiceImpl.generateJwt` lê o enum `Perfil` associado e preenche a claim `groups` com a string `"Administrador"` ou `"Usuario"`.
O Quarkus valida a claim `groups` via anotação `@RolesAllowed`.

### 1.3 Riscos e Consequências do Modelo Atual
1. **Escalada de Privilégio Imediata:** Como o endpoint `PUT /usuarios/{id}` e `POST /usuarios` recebem `idPerfil` livremente no corpo JSON sem autenticação ou validação de papel, qualquer usuário pode enviar `idPerfil: 1` e tornar-se Administrador.
2. **Ambiguidade de Entidades:** Um cliente pode ser manipulado tanto por endpoints `/clientes` quanto por `/usuarios`. Não há garantia de que um registro na tabela `usuario` com `id_perfil = 2` possua linha na tabela `cliente` (depende se foi criado por `/clientes` ou `/usuarios`).
3. **Senhas e Hashing:** O algoritmo PBKDF2 usa apenas 512 iterações e salt fixo global (`HashServiceImpl`), tornando o banco vulnerável a ataques de força bruta offline em caso de dump do PostgreSQL.

---

## 2. Comparativo Técnico das Estratégias de Herança JPA

A modelagem final exigida requer uma classe-mãe abstrata `Usuario`, da qual `Cliente` e `Administrador` herdam. Abaixo analisamos as três estratégias de herança suportadas pela especificação JPA/Hibernate:

| Critério | `SINGLE_TABLE` | `JOINED` | `TABLE_PER_CLASS` |
|---|---|---|---|
| **Estrutura no Banco** | 1 única tabela `usuario` com coluna discriminadora (ex.: `tipo_usuario`). | 3 tabelas: `usuario` (pai), `cliente` (filha) e `administrador` (filha). | Tabelas independentes `cliente` e `administrador` (cada uma repete colunas do pai). |
| **Integridade de Constraints (`NOT NULL`)** | **Ruim:** Atributos específicos das subclasses não podem ser `NOT NULL` no schema do banco (devem ser validados via software). | **Excelente:** Colunas específicas de `Cliente` ou `Administrador` podem ter `NOT NULL`, `UNIQUE` e chaves estrangeiras rígidas no PostgreSQL. | **Boa:** Permite `NOT NULL` em cada tabela, mas não permite chaves estrangeiras polimórficas diretas. |
| **Performance em Leituras Polimórficas** | **Excelente:** Leituras polimórficas (`SELECT u FROM Usuario u`) não exigem `JOIN`. | **Boa/Média:** Exige `LEFT OUTER JOIN` entre tabela pai e filhas ao buscar polimorficamente por `Usuario`. | **Péssima:** Exige `UNION ALL` no SQL para consultas polimórficas, inviabilizando paginação eficiente. |
| **Complexidade de Queries e DDL** | Mínima. Simples e direta. | Média. Relacionamento via Foreign Key de PK compartilhada (`@PrimaryKeyJoinColumn`). | Alta. Desencorajada pela maioria dos especialistas em JPA. |
| **Facilidade de Migração dos Dados Existentes** | Alta. A tabela `usuario` já existe. Apenas adiciona coluna discriminadora. | **Excelente:** O projeto **já utiliza `JOINED`** hoje para `Usuario` e `Cliente`. | Péssima. Exige dividir dados e quebrar foreign keys existentes. |

### Recomendação Técnica Justificada: `InheritanceType.JOINED`
Recomendamos a manutenção e refinamento da estratégia **`InheritanceType.JOINED`** pelos seguintes motivos fundamentados:
1. **Aderência ao Padrão Atual do Projeto:** O schema do PostgreSQL e as anotações do projeto já utilizam `JOINED`.
2. **Integridade Relacional Estrita:** Permite que campos futuros específicos de `Cliente` (ex.: limite de crédito, pontos de fidelidade, preferências de café) e de `Administrador` (ex.: matrícula, setor, nível de auditoria) tenham constraints `NOT NULL` e índices dedicados sem poluir a tabela pai com valores nulos.
3. **Polimorfismo Coerente:** Permite consultas genéricas na tabela pai para autenticação e login único (`Usuario`), garantindo ao mesmo tempo isolamento semântico claro entre o cliente do e-commerce e o operador interno (administrador).

---

## 3. Proposta de Modelagem para a Parte B

### 3.1 Definição das Entidades
```java
// Entidade-Mãe Abstrata
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario extends DefaultEntity {
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email; // Inclusão obrigatória de e-mail único

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String senha;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil; // Papel derivado e estrito

    // Getters e setters...
}

// Subclasse Cliente
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones;

    // Métodos e atributos de cliente...
}

// Subclasse Administrador
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id")
public class Administrador extends Usuario {
    @Column(length = 50)
    private String cargo;
}
```

### 3.2 Impacto nos Endpoints e Dados Existentes
| Endpoint Atual | Mudança Proposta | Impacto no Frontend Angular |
|---|---|---|
| `POST /usuarios` | **Extinguir** rota de criação genérica. A criação deve ocorrer em `POST /clientes` (autocadastro público) e `POST /administradores` (restrito a admin master). | O formulário de cadastro do Angular apontará para `/clientes`. |
| `GET /usuarios` | Restringir exclusivamente para administradores como visão técnica ou extinguir em favor de `GET /clientes` e `GET /administradores`. | A listagem geral de usuários do Angular consumirá `/clientes` ou `/administradores`. |
| `PUT /usuarios/{id}` | Desmembrar em `PUT /clientes/{id}` e `PUT /administradores/{id}` com validações de papel pelo token. Proibir alteração de papel (`perfil`) via DTO. | DTOs de atualização deixarão de enviar `idPerfil`. |
| `POST /auth/login` | O login continuará buscando na tabela pai `Usuario` por e-mail/login e senha, gerando as roles (`groups`) correspondentes à subclasse da entidade. | Nenhuma quebra no fluxo de login. |
