import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CafeService } from '../../services/cafe.service';
import { CategoriaDoCafeService } from '../../services/categoria-do-cafe.service';
import { CartService } from '../../services/cart.service';
import { Cafe, getCafeImage } from '../../models/cafe.model';
import { CategoriaDoCafe } from '../../models/categoria-do-cafe.model';

interface PerfilTorraInfo {
  id: number;
  nome: string;
  subtitulo: string;
  descricao: string;
  acidez: number;
  docura: number;
  corpo: number;
  metodoRecomendado: string;
  corBarra: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html'
})
export class HomeComponent implements OnInit {
  private readonly cafeService = inject(CafeService);
  private readonly categoriaService = inject(CategoriaDoCafeService);
  readonly cartService = inject(CartService);
  private readonly router = inject(Router);

  cafesDestaque = signal<Cafe[]>([]);
  categorias = signal<CategoriaDoCafe[]>([]);
  carregando = signal<boolean>(true);
  feedbackToast = signal<string | null>(null);

  readonly perfisTorra: PerfilTorraInfo[] = [
    {
      id: 1,
      nome: 'Torra Clara',
      subtitulo: 'Acidez Brilhante & Notas Florais',
      descricao:
        'Preserva as características originais do terroir e do fruto. Ideal para métodos filtrados como Hario V60, Chemex e Aeropress, revelando notas florais, cítricas e chá preto.',
      acidez: 90,
      docura: 75,
      corpo: 55,
      metodoRecomendado: 'Hario V60 • Chemex • Aeropress',
      corBarra: '#d98a4c'
    },
    {
      id: 2,
      nome: 'Torra Média',
      subtitulo: 'Equilíbrio Nobre & Doçura Caramelizada',
      descricao:
        'O ponto de ouro da Torrefação Dorana. Caramelização plena dos açúcares naturais do grão, entregando corpo aveludado, notas de chocolate ao leite, caramelo e frutas amarelas.',
      acidez: 65,
      docura: 95,
      corpo: 80,
      metodoRecomendado: 'Espresso • Prensa Francesa • Coador',
      corBarra: '#885031'
    },
    {
      id: 3,
      nome: 'Torra Escura',
      subtitulo: 'Corpo Denso & Finalização Intensa',
      descricao:
        'Desenvolvida para paladares que buscam intensidade, baixa acidez e notas profundas de cacau amargo, especiarias tostadas e nozes. Excelente para bebidas com leite e Moka Italiana.',
      acidez: 35,
      docura: 70,
      corpo: 98,
      metodoRecomendado: 'Moka Italiana • Espresso Intenso • Cappuccino',
      corBarra: '#3c2e28'
    }
  ];

  perfilTorraAtivo = signal<PerfilTorraInfo>(this.perfisTorra[1]);

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.carregando.set(true);
    this.cafeService.findAll().subscribe({
      next: cafes => {
        this.cafesDestaque.set((cafes || []).slice(0, 6));
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
      }
    });

    this.categoriaService.findAll().subscribe({
      next: cats => this.categorias.set(cats || []),
      error: () => {}
    });
  }

  selecionarPerfilTorra(perfil: PerfilTorraInfo): void {
    this.perfilTorraAtivo.set(perfil);
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

  adicionarRapido(cafe: Cafe, event: Event): void {
    event.stopPropagation();
    if ((cafe.estoque ?? 0) <= 0) return;
    this.cartService.addItem(cafe, 1, 'GRAOS');
    this.feedbackToast.set(`"${cafe.nome}" (Em Grãos) adicionado à sacola!`);
    setTimeout(() => this.feedbackToast.set(null), 3200);
  }

  irParaCatalogoCategoria(idCategoria: number): void {
    this.router.navigate(['/catalogo'], { queryParams: { categoria: idCategoria } });
  }
}
