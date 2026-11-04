# 05 - Proposta de Estrutura e Opções de Arquitetura

**Data:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 4 (Portão de Aprovação - Somente Leitura)  

---

## 1. Comparativo: Estrutura Atual vs. Estrutura Proposta

### Opção 1 (Recomendada): Package-by-Feature Enxuto com Pacotes em Minúsculas
Nesta opção, unificamos as entidades e seus DTOs/Resources/Services no pacote de domínio em minúsculas (ou mantemos subpastas mínimas apenas onde há múltiplos arquivos), eliminando o CamelCase e a proliferação de pastas de 1 único arquivo:

```text
[ESTRUTURA ATUAL (CamelCase + Hiperaninhada)]
br.unitins.topicos1.lgc/
├── Cafe/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   ├── resource/
│   └── service/
├── Perfil/
│   └── model/Perfil.java
├── Regiao/
│   └── model/Regiao.java
└── Usuario/
    ├── dto/
    ├── model/
    ├── repository/
    ├── resource/
    └── service/

[ESTRUTURA PROPOSTA (Minúsculas Padronizadas)]
br.unitins.topicos1.lgc/
├── cafe/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   ├── resource/
│   └── service/
├── perfil/
│   └── model/Perfil.java  (ou direto em perfil/Perfil.java)
├── regiao/
│   └── model/Regiao.java
└── usuario/
    ├── dto/
    ├── model/
    ├── repository/
    ├── resource/
    └── service/
```

### Opção 2: Package-by-Layer Canônico (Agrupamento por Camada Técnica)
Agrupar por camadas tradicionais (`model/`, `repository/`, `service/`, `resource/`, `dto/`):
- *Prós:* Menos diretórios na raiz de pacotes.
- *Contras:* Alta dispersão do domínio (para trabalhar em `Cafe`, navega-se entre 5 pastas distantes). Não recomendado para sistemas modernos com muitas entidades.

---

## 2. Convenção de Nomes Proposta
- **Backend:** Nomes de pacotes estritamente em minúsculas, palavras compostas sem separador conforme convenção oficial Java (ex.: `categoriadocafe`, `niveldetorra`, `notasensorial`, `regiaodeorigem`, `itempedido`).
- **Frontend:** Kebab-case (já adotado): `categoria-do-cafe`, `nivel-de-torra`, `item-pedido`.
- **Estratégia Git no Windows (`core.ignorecase=true`):**
  - Todas as movimentações de pastas serão executadas via `git mv <Nome> <nome>_tmp && git mv <nome>_tmp <nome>` para registrar as mudanças no índice do Git.

---

## 3. Dúvidas para Aprovação do Usuário

1. **Aprovação do Padrão de Nomenclatura:**
   - Confirma a conversão de todos os 28 diretórios do backend de CamelCase para minúsculas (ex.: `Cafe` → `cafe`, `CategoriaDoCafe` → `categoriadocafe`, `NotaSensorial` → `notasensorial`)?
2. **Nível de Profundidade:**
   - Deseja manter as subpastas funcionais (`dto`, `model`, `repository`, `resource`, `service`) sob a pasta da entidade em minúsculas (mantendo a separação limpa de responsabilidades e facilitando a transição), ou prefere colapsar algumas classes na raiz do pacote da entidade?
   - *(Recomendação: Manter as subpastas funcionais em minúsculas para entidades completas, e colapsar apenas enums isolados).*
3. **Commit de Segurança Prévio:**
   - Como há alterações das etapas anteriores na branch de trabalho, recomenda-se criar um commit de checkpoint na branch atual antes de iniciar a reestruturação física de arquivos. Confirma?
