# 02 - Análise da Estrutura Atual do Backend

**Data:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 1 (Análise da Estrutura e Inconsistências do Backend)  

---

## 1. Problema de Nomenclatura: Pastas em CamelCase / PascalCase

O backend Java em `lga-tp1-main/src/main/java/br/unitins/topicos1/lgc/` possui suas pastas organizadas com letras maiúsculas, contrariando a convenção Java (pacotes estritamente em minúsculas) e gerando inconsistência direta com o front-end Angular:

### Lista de Diretórios Afetados:
- `Administrador/`
- `Auth/`
- `Cafe/`
- `CategoriaDoCafe/`
- `CategoriaSensorial/`
- `Cliente/`
- `DefaultEntity/`
- `Endereco/`
- `Estado/`
- `Estoque/`
- `Exception/`
- `Frete/`
- `Hash/`
- `ItemPedido/`
- `JwT/`
- `Marca/`
- `Municipio/`
- `NivelDeTorra/`
- `NotaSensorial/`
- `Pagamento/`
- `Pedido/`
- `Perfil/`
- `Regiao/`
- `RegiaoDeOrigem/`
- `Security/`
- `Telefone/`
- `Tratamento/`
- `Usuario/`
- *(Apenas `jpa/` estava em minúsculas)*.

---

## 2. Problema de Profundidade Desnecessária de Camadas

A organização interna de cada entidade no backend sofre de **hiperfragmentação de subpacotes**. Cada entidade possui até 5 subdiretórios rasos:

```text
br.unitins.topicos1.lgc.Cafe/
├── dto/
│   ├── CafeDTO.java
│   └── CafeDTOResponse.java
├── model/
│   └── Cafe.java
├── repository/
│   └── CafeRepository.java
├── resource/
│   └── CafeResource.java
└── service/
    ├── CafeService.java
    └── CafeServiceImpl.java
```

Além disso, entidades simples que contêm apenas 1 arquivo (como enums ou superclasses) também foram empacotadas com subdiretórios redundantes:
- `br.unitins.topicos1.lgc.Perfil/model/Perfil.java` (apenas 1 arquivo dentro de subpasta `model`)
- `br.unitins.topicos1.lgc.Regiao/model/Regiao.java` (apenas 1 arquivo dentro de subpasta `model`)
- `br.unitins.topicos1.lgc.DefaultEntity/model/DefaultEntity.java` (apenas 1 arquivo)

---

## 3. Comparativo com o Front-end

No frontend Angular (`lga-tp1-main/frontend/src/app/`), a estrutura é mais enxuta:
- Nomes de diretórios todos em minúsculas (kebab-case): `components/cafes/cafe-list`, `models/cafe.model.ts`, `services/cafe.service.ts`.
- Não há aninhamento de 5 níveis por entidade.
