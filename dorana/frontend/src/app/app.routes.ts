import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home';
import { CatalogoComponent } from './components/catalogo/catalogo';
import { ProdutoDetalheComponent } from './components/produto-detalhe/produto-detalhe';
import { CarrinhoComponent } from './components/carrinho/carrinho';
import { CheckoutComponent } from './components/checkout/checkout';
import { MinhaContaComponent } from './components/minha-conta/minha-conta';
import { AdminDashboardComponent } from './components/admin/dashboard/admin-dashboard';
import { EstoqueListComponent } from './components/estoque/estoque-list';

import { LoginComponent } from './components/auth/login/login';

import { EstadoListComponent } from './components/estados/estado-list/estado-list';
import { EstadoFormComponent } from './components/estados/estado-form/estado-form';
import { estadoResolver } from './resolvers/estado-resolver';

import { UsuarioListComponent } from './components/usuarios/usuario-list/usuario-list';
import { UsuarioFormComponent } from './components/usuarios/usuario-form/usuario-form';
import { usuarioResolver } from './resolvers/usuario-resolver';

import { MunicipioListComponent } from './components/municipios/municipio-list/municipio-list';
import { MunicipioFormComponent } from './components/municipios/municipio-form/municipio-form';
import { municipioResolver } from './resolvers/municipio-resolver';

import { CafeListComponent } from './components/cafes/cafe-list/cafe-list';
import { CafeFormComponent } from './components/cafes/cafe-form/cafe-form';
import { cafeResolver } from './resolvers/cafe-resolver';

import { MarcaListComponent } from './components/marcas/marca-list/marca-list';
import { MarcaFormComponent } from './components/marcas/marca-form/marca-form';
import { marcaResolver } from './resolvers/marca-resolver';

import { CategoriaListComponent } from './components/categorias/categoria-list/categoria-list';
import { CategoriaFormComponent } from './components/categorias/categoria-form/categoria-form';
import { categoriaDoCafeResolver } from './resolvers/categoria-do-cafe-resolver';

export const routes: Routes = [
  // VITRINE E-COMMERCE DORANA COFFEE SHOP
  {
    path: '',
    component: HomeComponent,
    title: 'Dorana Coffee Shop | Cafés Especiais & Torrefação Artesanal'
  },
  {
    path: 'home',
    redirectTo: '',
    pathMatch: 'full'
  },
  {
    path: 'catalogo',
    component: CatalogoComponent,
    title: 'Catálogo de Grãos | Dorana Coffee Shop'
  },
  {
    path: 'produto/:id',
    component: ProdutoDetalheComponent,
    title: 'Detalhes do Microlote | Dorana Coffee Shop'
  },
  {
    path: 'carrinho',
    component: CarrinhoComponent,
    title: 'Sacola de Compras | Dorana Coffee Shop'
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    title: 'Checkout & Pagamento | Dorana Coffee Shop'
  },
  {
    path: 'minha-conta',
    component: MinhaContaComponent,
    title: 'Minha Conta | Dorana Coffee Shop'
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'Autenticação & Cadastro | Dorana Coffee Shop'
  },

  // BACKOFFICE & PAINEL ADMINISTRATIVO DORANA COFFEE SHOP
  {
    path: 'admin',
    component: AdminDashboardComponent,
    title: 'Painel Administrativo | Dorana Coffee Shop'
  },
  {
    path: 'estoque',
    component: EstoqueListComponent,
    title: 'Controle de Estoque | Dorana Coffee Shop'
  },

  // ROUTES DE USUARIOS
  {
    path: 'usuarios',
    component: UsuarioListComponent,
    title: 'Gestão de Usuários | Dorana Coffee Shop'
  },
  {
    path: 'usuarios/new',
    component: UsuarioFormComponent,
    title: 'Novo Usuário | Dorana Coffee Shop'
  },
  {
    path: 'usuarios/edit/:id',
    component: UsuarioFormComponent,
    title: 'Editar Usuário | Dorana Coffee Shop',
    resolve: {
      usuario: usuarioResolver
    }
  },

  // ROUTES DE ESTADOS
  {
    path: 'estados',
    component: EstadoListComponent,
    title: 'Gestão de Estados | Dorana Coffee Shop'
  },
  {
    path: 'estados/new',
    component: EstadoFormComponent,
    title: 'Novo Estado | Dorana Coffee Shop'
  },
  {
    path: 'estados/edit/:id',
    component: EstadoFormComponent,
    title: 'Editar Estado | Dorana Coffee Shop',
    resolve: {
      estado: estadoResolver
    }
  },

  // ROUTES DE MUNICIPIOS
  {
    path: 'municipios',
    component: MunicipioListComponent,
    title: 'Gestão de Municípios | Dorana Coffee Shop'
  },
  {
    path: 'municipios/new',
    component: MunicipioFormComponent,
    title: 'Novo Município | Dorana Coffee Shop'
  },
  {
    path: 'municipios/edit/:id',
    component: MunicipioFormComponent,
    title: 'Editar Município | Dorana Coffee Shop',
    resolve: {
      municipio: municipioResolver
    }
  },

  // ROUTES DE CAFE
  {
    path: 'cafes',
    component: CafeListComponent,
    title: 'Gestão de Cafés | Dorana Coffee Shop'
  },
  {
    path: 'cafes/new',
    component: CafeFormComponent,
    title: 'Novo Café | Dorana Coffee Shop'
  },
  {
    path: 'cafes/edit/:id',
    component: CafeFormComponent,
    title: 'Editar Café | Dorana Coffee Shop',
    resolve: {
      cafe: cafeResolver
    }
  },

  // ROUTES DE MARCAS
  {
    path: 'marcas',
    component: MarcaListComponent,
    title: 'Gestão de Marcas | Dorana Coffee Shop'
  },
  {
    path: 'marcas/new',
    component: MarcaFormComponent,
    title: 'Nova Marca | Dorana Coffee Shop'
  },
  {
    path: 'marcas/edit/:id',
    component: MarcaFormComponent,
    title: 'Editar Marca | Dorana Coffee Shop',
    resolve: {
      marca: marcaResolver
    }
  },

  // ROUTES DE CATEGORIAS
  {
    path: 'categorias',
    component: CategoriaListComponent,
    title: 'Gestão de Categorias | Dorana Coffee Shop'
  },
  {
    path: 'categorias/new',
    component: CategoriaFormComponent,
    title: 'Nova Categoria | Dorana Coffee Shop'
  },
  {
    path: 'categorias/edit/:id',
    component: CategoriaFormComponent,
    title: 'Editar Categoria | Dorana Coffee Shop',
    resolve: {
      categoria: categoriaDoCafeResolver
    }
  },

  // FALLBACK
  {
    path: '**',
    redirectTo: ''
  }
];
