import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CafeService } from '../../services/cafe.service';
import { CategoriaDoCafeService } from '../../services/categoria-do-cafe.service';
import { MarcaService } from '../../services/marca.service';
import { CartService } from '../../services/cart.service';
import {
  Cafe,
  NIVEIS_DE_TORRA_OPCOES,
  TRATAMENTOS_OPCOES,
  getCafeImage
} from '../../models/cafe.model';
import { CategoriaDoCafe } from '../../models/categoria-do-cafe.model';
import { Marca } from '../../models/marca.model';
import { OPCOES_MOAGEM, TipoMoagem } from '../../models/cart-item.model';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './catalogo.html'
})
export class CatalogoComponent implements OnInit {
  private readonly cafeService = inject(CafeService);
  private readonly categoriaService = inject(CategoriaDoCafeService);
  private readonly marcaService = inject(MarcaService);
  private readonly route = inject(ActivatedRoute);
  readonly cartService = inject(CartService);

  todosCafes = signal<Cafe[]>([]);
  categorias = signal<CategoriaDoCafe[]>([]);
  marcas = signal<Marca[]>([]);
  carregando = signal<boolean>(true);

  // Estado dos Filtros
  termoBusca = signal<string>('');
  categoriaSelecionada = signal<number | null>(null);
  marcaSelecionada = signal<number | null>(null);
  torraSelecionada = signal<number | null>(null);
  tratamentoSelecionado = signal<number | null>(null);
  scaMinimo = signal<number>(0);
  apenasEmEstoque = signal<boolean>(false);
  ordenacao = signal<string>('sca_desc');

  // Modal de Seleção de Moagem Rápida
  cafeModalMoagem = signal<Cafe | null>(null);
  moagemEscolhida = signal<TipoMoagem>('GRAOS');
  quantidadeModal = signal<number>(1);
  feedbackToast = signal<string | null>(null);

  readonly niveisTorra = NIVEIS_DE_TORRA_OPCOES;
  readonly tratamentos = TRATAMENTOS_OPCOES;
  readonly opcoesMoagem = OPCOES_MOAGEM;

  readonly cafesFiltrados = computed(() => {
    const busca = this.termoBusca().trim().toLowerCase();
    const idCat = this.categoriaSelecionada();
    const idMarca = this.marcaSelecionada();
    const idTorra = this.torraSelecionada();
    const idTrat = this.tratamentoSelecionado();
    const minSca = this.scaMinimo();
    const soEstoque = this.apenasEmEstoque();
    const ordem = this.ordenacao();

    const filtrados = this.todosCafes().filter(c => {
      if (busca) {
        const matchNome = (c.nome || '').toLowerCase().includes(busca);
        const matchDesc = (c.descricao || '').toLowerCase().includes(busca);
        const matchMarca = (c.marca?.nome || '').toLowerCase().includes(busca);
        const matchNota = (c.notasSensoriais || []).some(n =>
          (n.nome || '').toLowerCase().includes(busca)
        );
        if (!matchNome && !matchDesc && !matchMarca && !matchNota) {
          return false;
        }
      }
      if (idCat !== null && c.categoriaDoCafe?.id !== idCat) return false;
      if (idMarca !== null && c.marca?.id !== idMarca) return false;
      if (idTorra !== null && c.nivelDeTorra?.id !== idTorra) return false;
      if (idTrat !== null && c.tratamento?.id !== idTrat) return false;
      if (minSca > 0 && (c.pontuacaoSCA ?? 0) < minSca) return false;
      if (soEstoque && (c.estoque ?? 0) <= 0) return false;
      return true;
    });

    return [...filtrados].sort((a, b) => {
      switch (ordem) {
        case 'preco_asc':
          return (a.preco || 0) - (b.preco || 0);
        case 'preco_desc':
          return (b.preco || 0) - (a.preco || 0);
        case 'nome_asc':
          return (a.nome || '').localeCompare(b.nome || '');
        case 'sca_desc':
        default:
          return (b.pontuacaoSCA || 0) - (a.pontuacaoSCA || 0);
      }
    });
  });

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const q = params.get('q');
      const cat = params.get('categoria');
      const torra = params.get('torra');
      if (q !== null) this.termoBusca.set(q);
      if (cat !== null) this.categoriaSelecionada.set(Number(cat));
      if (torra !== null) this.torraSelecionada.set(Number(torra));
    });

    this.carregarCatalogo();
  }

  carregarCatalogo(): void {
    this.carregando.set(true);
    this.cafeService.findAll().subscribe({
      next: cafes => {
        this.todosCafes.set(cafes || []);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false)
    });

    this.categoriaService.findAll().subscribe({
      next: cats => this.categorias.set(cats || []),
      error: () => {}
    });

    this.marcaService.findAll().subscribe({
      next: marcas => this.marcas.set(marcas || []),
      error: () => {}
    });
  }

  limparFiltros(): void {
    this.termoBusca.set('');
    this.categoriaSelecionada.set(null);
    this.marcaSelecionada.set(null);
    this.torraSelecionada.set(null);
    this.tratamentoSelecionado.set(null);
    this.scaMinimo.set(0);
    this.apenasEmEstoque.set(false);
  }

  obterImagem(cafe: Cafe, index: number): string {
    return getCafeImage(cafe, index);
  }

  obterLarguraTorra(cafe: Cafe): string {
    const id = cafe.nivelDeTorra?.id;
    const nome = (cafe.nivelDeTorra?.nome || '').toLowerCase();
    if (id === 1 || nome.includes('clara')) return '33%';
    if (id === 3 || nome.includes('escura')) return '100%';
    return '66%';
  }

  abrirSeletorMoagem(cafe: Cafe): void {
    if ((cafe.estoque ?? 0) <= 0) return;
    this.cafeModalMoagem.set(cafe);
    this.moagemEscolhida.set('GRAOS');
    this.quantidadeModal.set(1);
  }

  fecharSeletorMoagem(): void {
    this.cafeModalMoagem.set(null);
  }

  confirmarAdicaoCarrinho(): void {
    const cafe = this.cafeModalMoagem();
    if (!cafe) return;
    this.cartService.addItem(cafe, this.quantidadeModal(), this.moagemEscolhida());
    this.fecharSeletorMoagem();
    this.feedbackToast.set(`"${cafe.nome}" adicionado à sacola!`);
    setTimeout(() => this.feedbackToast.set(null), 3200);
  }
}
