import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CafeService } from '../../../services/cafe.service';
import { PedidoService } from '../../../services/pedido.service';
import { MarcaService } from '../../../services/marca.service';
import { CategoriaDoCafeService } from '../../../services/categoria-do-cafe.service';
import { UsuarioService } from '../../../services/usuario.service';
import { Cafe, getCafeImage } from '../../../models/cafe.model';
import { PedidoDTOResponse } from '../../../models/pedido.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.html'
})
export class AdminDashboardComponent implements OnInit {
  private readonly cafeService = inject(CafeService);
  private readonly pedidoService = inject(PedidoService);
  private readonly marcaService = inject(MarcaService);
  private readonly categoriaService = inject(CategoriaDoCafeService);
  private readonly usuarioService = inject(UsuarioService);

  cafes = signal<Cafe[]>([]);
  pedidos = signal<PedidoDTOResponse[]>([]);
  totalMarcas = signal<number>(0);
  totalCategorias = signal<number>(0);
  totalUsuarios = signal<number>(0);
  carregando = signal<boolean>(true);
  feedbackMsg = signal<string | null>(null);

  readonly receitaTotal = computed(() =>
    this.pedidos()
      .filter(p => p.status?.id !== 6)
      .reduce((acc, p) => acc + (p.totalPedido || 0), 0)
  );

  readonly mediaScaCatalogo = computed(() => {
    const lista = this.cafes().filter(c => (c.pontuacaoSCA ?? 0) > 0);
    if (lista.length === 0) return 86.0;
    const soma = lista.reduce((acc, c) => acc + (c.pontuacaoSCA || 0), 0);
    return soma / lista.length;
  });

  readonly cafesEstoqueCritico = computed(() =>
    this.cafes().filter(c => (c.estoque ?? 0) <= 5)
  );

  readonly totalUnidadesEstoque = computed(() =>
    this.cafes().reduce((acc, c) => acc + (c.estoque || 0), 0)
  );

  ngOnInit(): void {
    this.carregarIndicadores();
  }

  carregarIndicadores(): void {
    this.carregando.set(true);

    this.cafeService.findAll().subscribe({
      next: lista => {
        this.cafes.set(lista || []);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false)
    });

    this.pedidoService.findAll().subscribe({
      next: lista => this.pedidos.set(lista || []),
      error: () => {}
    });

    this.marcaService.findAll().subscribe({
      next: m => this.totalMarcas.set((m || []).length),
      error: () => {}
    });

    this.categoriaService.findAll().subscribe({
      next: c => this.totalCategorias.set((c || []).length),
      error: () => {}
    });

    this.usuarioService.findAll().subscribe({
      next: u => this.totalUsuarios.set((u || []).length),
      error: () => {}
    });
  }

  obterImagem(cafe: Cafe, idx: number): string {
    return getCafeImage(cafe, idx);
  }

  cancelarPedidoAdmin(idPedido: number): void {
    this.pedidoService.cancelar(idPedido).subscribe({
      next: () => {
        this.feedbackMsg.set(`Pedido #${idPedido} cancelado e estoque estornado.`);
        setTimeout(() => this.feedbackMsg.set(null), 3200);
        this.carregarIndicadores();
      },
      error: () => {}
    });
  }
}
