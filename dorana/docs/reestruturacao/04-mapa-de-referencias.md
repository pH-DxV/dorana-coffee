# 04 - Mapa de Referências e Impactos

**Data:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 3 (Caça às Referências do Backend)  

---

## 1. Referências em Classes Java (Imports e Packages)

A conversão das pastas do backend de CamelCase para minúsculas afetará a declaração `package` e todos os `import` em aproximadamente 75 classes Java e 13 classes de teste.

### Exemplo de Migração de Declarações:
- De: `package br.unitins.topicos1.lgc.Cafe.model;`
- Para: `package br.unitins.topicos1.lgc.cafe.model;` (ou `br.unitins.topicos1.lgc.cafe;` na estrutura compacta).
- De: `package br.unitins.topicos1.lgc.Usuario.resource;`
- Para: `package br.unitins.topicos1.lgc.usuario.resource;`

---

## 2. Referências em Arquivos de Configuração e Recursos

- **`application.properties`:**
  - Configurações de log por pacote (ex.: `quarkus.log.category."br.unitins.topicos1.lgc".level`).
  - Não há referências fixas em maiúsculas em `quarkus.hibernate-orm.packages`.
- **`import.sql`:**
  - Inserts de dados SQL e DDL referenciam nomes de tabelas em minúsculas (`cafe`, `usuario`, `cliente`, `administrador`, etc.), não sofrendo impacto.
- **Classes de Teste:**
  - As 13 classes de teste (`CafeResourceTest`, `UsuarioResourceTest`, etc.) importam entidades e DTOs usando os pacotes com maiúsculas. Todos esses imports deverão ser atualizados.
- **Visibilidade de Membros:**
  - Mapeamento: os métodos dos services (`findById`, `create`, `update`, etc.) e entidades JPA são `public`, portanto não haverá quebra de acesso por visibilidade `package-private`.
