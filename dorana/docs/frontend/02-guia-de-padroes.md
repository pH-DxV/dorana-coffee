# 02 - Guia de Padrões do Front-end

**Data da Análise:** 04/11/2026  
**Auditor / Engenheiro:** Antigravity  
**Fase:** Fase 1 (Extração de Padrões e Módulos de Referência)  

---

## 1. Módulos de Referência Identificados

Os módulos mais consistentes e completos da base são:
- **Estados:** [`EstadoListComponent`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/components/estados/estado-list/estado-list.ts) e [`EstadoFormComponent`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/components/estados/estado-form/estado-form.ts).
- **Usuários:** [`UsuarioListComponent`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/components/usuarios/usuario-list/usuario-list.ts) e [`UsuarioFormComponent`](file:///C:/Users/raphaelocl/Documents/Raphael/AntiGravity/coffehouse/lga-tp1-main/frontend/src/app/components/usuarios/usuario-form/usuario-form.ts).

---

## 2. Anatomia Padrão de uma Feature (O "Molde")

Cada entidade/feature segue uma convenção estrita de 4 camadas:

```text
src/app/
├── models/<entidade>.model.ts
├── services/<entidade>.service.ts
├── resolvers/<entidade>-resolver.ts
└── components/<entidade-plural>/
    ├── <entidade>-list/
    │   ├── <entidade>-list.ts
    │   ├── <entidade>-list.html
    │   └── <entidade>-list.css
    └── <entidade>-form/
        ├── <entidade>-form.ts
        ├── <entidade>-form.html
        └── <entidade>-form.css
```

---

## 3. Padrões por Aspecto Técnico

### 3.1. Listagens (`<feature>-list`)
- **Tabela Angular Material:** Utiliza `MatTableModule` com `MatTableDataSource<T>`.
- **Toolbar Superior:**
  - `<mat-toolbar>` com título descritivo (`.toolbar-title`) e botão flutuante de criação (`<button mat-fab class="accent" routerLink="/.../new"> <mat-icon>add</mat-icon> </button>`).
- **Filtro em Memória:**
  - Campo `<mat-form-field appearance="outline">` com evento `(keyup)="applyFilter($event)"`.
  - Implementação via `this.dataSource.filter = filterValue.trim().toLowerCase();`.
- **Paginação:**
  - Componente `<mat-paginator [pageSize]="5" [pageSizeOptions]="[5, 10, 20]" showFirstLastButtons>`.
  - Vinculado no ciclo `ngAfterViewInit()`: `this.dataSource.paginator = this.paginator;`.
  - **Atenção:** A paginação atual é client-side (em memória). A API envia o array completo e o `MatTableDataSource` divide em páginas.
- **Ações na Linha:**
  - Botão de edição: `<button mat-icon-button color="primary" routerLink="/.../edit/{{obj.id}}"><mat-icon>edit</mat-icon></button>`.
  - Botão de exclusão: `<button mat-icon-button color="warn" (click)="excluir(obj.id)"><mat-icon>delete</mat-icon></button>`.

### 3.2. Formulários (`<feature>-form`)
- **Tecnologia:** Formulários Reativos (`ReactiveFormsModule`, `FormBuilder`, `FormGroup`).
- **Layout:** Estruturado com `<mat-card>`, campos com `<mat-form-field appearance="outline">`.
- **Ciclo Novo vs. Editar:**
  - Uso de `ActivatedRoute.snapshot.data['<entidade>']` obtido através do resolver correspondente na rota (`resolve: { <entidade>: <entidade>Resolver }`).
  - Se a entidade existir nos dados da rota, popula o formulário com `this.form.patchValue(entidade)`.
  - No método `salvar()`, se `form.value.id` existir executa `service.update(id, obj)`, caso contrário `service.create(obj)`.
- **Sub-coleções Dinâmicas (ex: Usuário):**
  - Implementadas via `FormArray` com métodos explícitos (`adicionarTelefone()`, `removerTelefone()`, `adicionarEndereco()`).
- **Feedback:**
  - Uso de `MatSnackBar` com duração de 2500ms posicionado no topo (`verticalPosition: 'top'`, `horizontalPosition: 'center'`).
  - Navegação pós-sucesso via `this.router.navigate(['/...'])`.
- **Navegação de Retorno:**
  - Injeção de `Location` do `@angular/common` com método `this.location.back()`.

### 3.3. Serviços (`<feature>.service.ts`)
- Decorados com `@Injectable({ providedIn: 'root' })`.
- Métodos CRUD padronizados:
  - `findAll(): Observable<T[]>`
  - `findById(id: any): Observable<T>`
  - `create(obj: T): Observable<T>`
  - `update(id: number, obj: T): Observable<T>`
  - `delete(id: number): Observable<void>`

---

## 4. Divergências e Inconsistências Detectadas

1. **Tipagem de ID nos Serviços:**
   - Em `estado.service.ts`: `findById(id: any)`.
   - Em `usuario.service.ts`: `findById(id: number)`.
   - Em `cafe.service.ts`: `findById(id: number)`.
   *Recomendação:* Padronizar todos os métodos com `id: number`.

2. **Validações de Formulário:**
   - Nenhum formulário atual define validadores síncronos do Angular (como `Validators.required`, `Validators.email`, `Validators.minLength`). Os campos são instanciados como `['']` ou `[null]`.
   *Recomendação:* Adicionar validações reativas essenciais para evitar erros HTTP 400 antes do envio.

3. **Gerenciamento de Assinaturas RxJS:**
   - Todas as chamadas usam `.subscribe()` sem desinscrição (`takeUntil`, `takeUntilDestroyed` ou `first()`). Não há uso do pipe `async`.

4. **Tratamento de Exclusão:**
   - O método `excluir()` remove o item direto ao clicar, sem exibir diálogo de confirmação (`MatDialog` ou `confirm()`).
