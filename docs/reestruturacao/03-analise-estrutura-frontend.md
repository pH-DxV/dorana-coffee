# 03 - Análise da Estrutura Atual do Frontend

**Data:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 2 (Análise da Estrutura do Frontend)  

---

## 1. Estrutura de Diretórios Atual do Frontend

O front-end Angular (`lga-tp1-main/frontend/src/app/`) já adota o padrão em minúsculas com separação de palavras por hífen (kebab-case):

```text
src/app/
├── components/
│   ├── auth/login/
│   ├── cafes/cafe-form/
│   ├── cafes/cafe-list/
│   ├── categorias/categoria-form/
│   ├── categorias/categoria-list/
│   ├── estados/estado-form/
│   ├── estados/estado-list/
│   ├── marcas/marca-form/
│   ├── marcas/marca-list/
│   ├── municipios/municipio-form/
│   ├── municipios/municipio-list/
│   ├── navbar/
│   ├── usuarios/usuario-form/
│   └── usuarios/usuario-list/
├── interceptors/
├── models/
├── resolvers/
└── services/
```

---

## 2. Diagnóstico e Padronização

- **Nomenclatura:** 100% dos diretórios e arquivos do frontend já estão em minúsculas / kebab-case (conforme exigido pela convenção oficial do Angular).
- **Imports:** Não há imports em CamelCase ou inconsistentes.
- **Ponto de Ajuste Estrutural na Raiz:** Atualmente, a pasta do frontend está localizada em `lga-tp1-main/frontend`, enquanto a documentação e o script referem-se à organização ideal onde backend e frontend ficam lado a lado como pastas irmãs na raiz do repositório, ou mantêm o padrão unificado de minúsculas.
