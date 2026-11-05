import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CafeService } from '../../services/cafe.service';
import { Cafe, getCafeImage } from '../../models/cafe.model';

export type FiltroEstoque = 'TODOS' | 'SAUDAVEL' | 'CRITICO' | 'ESGOTADO';
export type TipoMovimentacao = 'ENTRADA' | 'SAIDA' | 'DEFINIR';

@Component({
  selector: 'app-estoque-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './estoque-list.html'
})
export class EstoqueListComponent implements OnInit {
  private readonly cafeService = inject(CafeService);

  cafes = signal<Cafe[]>([]);
  carregando = signal<boolean>(true);
  termoBusca = signal<string>('');
  filtroStatus = signal<FiltroEstoque>('TODOS');
  feedbackMsg = signal<string | null>(null);
  erroMsg = signal<string | null>(null);

  // Modal de Movimentação de Estoque
  cafeSelecionadoModal = signal<Cafe | null>(null);
  tipoMovimentacao = signal<TipoMovimentacao>('ENTRADA');
  quantidadeMovimentacao = signal<number>(10);
  salvandoMovimentacao = signal<boolean>(false);

  readonly totalPacotes = computed(() =>
    this.cafes().reduce((acc, c) => acc + (c.estoque || 0), 0)
  );

  readonly valorPatrimonial = computed(() =>
    this.cafes().reduce((acc, c) => acc + (c.preco || 0) * (c.estoque || 0), 0)
  );

  readonly qtdEstoqueCritico = computed(() =>
    this.cafes().filter(c => (c.estoque ?? 0) > 0 && (c.estoque ?? 0) <= 5).length
  );

  readonly qtdEsgotados = computed(() =>
    this.cafes().filter(c => (c.estoque ?? 0) <= 0).length
  );

  readonly cafesFiltrados = computed(() => {
    const q = this.termoBusca().trim().toLowerCase();
    const status = this.filtroStatus();

    return this.cafes().filter(c => {
      if (q) {
        const match =
          (c.nome || '').toLowerCase().includes(q) ||
          (c.marca?.nome || '').toLowerCase().includes(q) ||
          (c.categoriaDoCafe?.nome || '').toLowerCase().includes(q);
        if (!match) return false;
      }
      const est = c.estoque ?? 0;
      if (status === 'SAUDAVEL') return est > 5;
      if (status === 'CRITICO') return est > 0 && est <= 5;
      if (status === 'ESGOTADO') return est <= 0;
      return true;
    });
  });

  ngOnInit(): void {
    this.carregarEstoque();
  }

  carregarEstoque(): void {
    this.carregando.set(true);
    this.cafeService.findAll().subscribe({
      next: lista => {
        this.cafes.set(lista || []);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false)
    });
  }

  obterImagem(cafe: Cafe, idx: number): string {
    return getCafeImage(cafe, idx);
  }

  abrirModalMovimentacao(cafe: Cafe, tipo: TipoMovimentacao = 'ENTRADA'): void {
    this.cafeSelecionadoModal.set(cafe);
    this.tipoMovimentacao.set(tipo);
    this.quantidadeMovimentacao.set(tipo === 'DEFINIR' ? (cafe.estoque ?? 0) : 10);
  }

  fecharModalMovimentacao(): void {
    this.cafeSelecionadoModal.set(null);
  }

  calcularNovoSaldoPreview(): number {
    const c = this.cafeSelecionadoModal();
    if (!c) return 0;
    const atual = c.estoque ?? 0;
    const qtd = Math.max(0, Number(this.quantidadeMovimentacao()) || 0);
    if (this.tipoMovimentacao() === 'ENTRADA') return atual + qtd;
    if (this.tipoMovimentacao() === 'SAIDA') return Math.max(0, atual - qtd);
    return qtd;
  }

  confirmarMovimentacao(): void {
    const c = this.cafeSelecionadoModal();
    if (!c) return;

    const novoEstoque = this.calcularNovoSaldoPreview();
    const payload = this.cafeService.toPayload(c, novoEstoque);

    this.salvandoMovimentacao.set(true);
    this.erroMsg.set(null);

    this.cafeService.update(c.id, payload).subscribe({
      next: atualizado => {
        this.salvandoMovimentacao.set(false);
        this.cafes.update(lista =>
          lista.map(item => (item.id === c.id ? { ...item, ...atualizado, estoque: novoEstoque } : item))
        );
        this.fecharModalMovimentacao();
        this.feedbackMsg.set(
          `Estoque de "${c.nome}" atualizado para ${novoEstoque} unidades.`
        );
        setTimeout(() => this.feedbackMsg.set(null), 3500);
      },
      error: err => {
        this.salvandoMovimentacao.set(false);
        this.erroMsg.set(
          err?.error?.message || 'Erro ao atualizar saldo de estoque na API.'
        );
      }
    });
  }
}
