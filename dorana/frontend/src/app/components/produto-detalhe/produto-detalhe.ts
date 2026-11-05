import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CafeService } from '../../services/cafe.service';
import { CartService } from '../../services/cart.service';
import { Cafe, STITCH_CAFE_IMAGES, getCafeImage } from '../../models/cafe.model';
import { OPCOES_MOAGEM, TipoMoagem } from '../../models/cart-item.model';

@Component({
  selector: 'app-produto-detalhe',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './produto-detalhe.html'
})
export class ProdutoDetalheComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cafeService = inject(CafeService);
  readonly cartService = inject(CartService);

  cafe = signal<Cafe | null>(null);
  relacionados = signal<Cafe[]>([]);
  carregando = signal<boolean>(true);
  imagemAtiva = signal<string>('');
  galeria = signal<string[]>([]);

  moagemSelecionada = signal<TipoMoagem>('GRAOS');
  quantidade = signal<number>(1);
  feedbackToast = signal<string | null>(null);

  readonly opcoesMoagem = OPCOES_MOAGEM;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) {
        this.carregarProduto(id);
      }
    });
  }

  carregarProduto(id: number): void {
    this.carregando.set(true);
    this.cafeService.findById(id).subscribe({
      next: c => {
        this.cafe.set(c);
        const imgPrincipal = getCafeImage(c);
        const outras = STITCH_CAFE_IMAGES.filter(img => img !== imgPrincipal).slice(0, 3);
        this.galeria.set([imgPrincipal, ...outras]);
        this.imagemAtiva.set(imgPrincipal);
        this.quantidade.set(1);
        this.carregando.set(false);
        this.carregarRelacionados(c.id);
      },
      error: () => {
        this.carregando.set(false);
      }
    });
  }

  carregarRelacionados(idAtual: number): void {
    this.cafeService.findAll().subscribe({
      next: lista => {
        this.relacionados.set((lista || []).filter(item => item.id !== idAtual).slice(0, 3));
      },
      error: () => {}
    });
  }

  obterImagem(cafe: Cafe, idx = 0): string {
    return getCafeImage(cafe, idx);
  }

  obterLarguraTorra(cafe: Cafe): string {
    const id = cafe.nivelDeTorra?.id;
    const nome = (cafe.nivelDeTorra?.nome || '').toLowerCase();
    if (id === 1 || nome.includes('clara')) return '33%';
    if (id === 3 || nome.includes('escura')) return '100%';
    return '66%';
  }

  alterarQuantidade(delta: number): void {
    const c = this.cafe();
    if (!c) return;
    const max = c.estoque ?? 99;
    const nova = Math.min(max, Math.max(1, this.quantidade() + delta));
    this.quantidade.set(nova);
  }

  adicionarAoCarrinho(irParaSacola = false): void {
    const c = this.cafe();
    if (!c || (c.estoque ?? 0) <= 0) return;
    this.cartService.addItem(c, this.quantidade(), this.moagemSelecionada());
    if (irParaSacola) {
      this.router.navigate(['/carrinho']);
      return;
    }
    this.feedbackToast.set(`${this.quantidade()}x "${c.nome}" adicionado à sacola!`);
    setTimeout(() => this.feedbackToast.set(null), 3200);
  }
}
