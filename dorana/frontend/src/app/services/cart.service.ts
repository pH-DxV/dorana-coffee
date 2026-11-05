import { Injectable, computed, signal } from '@angular/core';
import { Cafe } from '../models/cafe.model';
import { CartItem, OPCOES_MOAGEM, TipoMoagem } from '../models/cart-item.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly STORAGE_KEY = 'dorana_cart_items';
  readonly FRETE_PADRAO = 15.0;
  readonly LIMITE_FRETE_GRATIS = 180.0;

  readonly items = signal<CartItem[]>(this.loadFromStorage());

  readonly totalCount = computed(() =>
    this.items().reduce((sum, item) => sum + item.quantidade, 0)
  );

  readonly subtotal = computed(() =>
    this.items().reduce((sum, item) => sum + (item.cafe.preco || 0) * item.quantidade, 0)
  );

  readonly frete = computed(() => {
    const sub = this.subtotal();
    if (sub === 0) return 0;
    return sub >= this.LIMITE_FRETE_GRATIS ? 0 : this.FRETE_PADRAO;
  });

  readonly total = computed(() => this.subtotal() + this.frete());

  readonly faltaParaFreteGratis = computed(() =>
    Math.max(0, this.LIMITE_FRETE_GRATIS - this.subtotal())
  );

  readonly progressoFreteGratis = computed(() =>
    Math.min(100, Math.round((this.subtotal() / this.LIMITE_FRETE_GRATIS) * 100))
  );

  addItem(cafe: Cafe, quantidade = 1, moagem: TipoMoagem = 'GRAOS'): void {
    const opcao = OPCOES_MOAGEM.find(o => o.id === moagem) || OPCOES_MOAGEM[0];
    const current = [...this.items()];
    const existingIdx = current.findIndex(
      i => i.cafe.id === cafe.id && i.moagem === moagem
    );

    const maxEstoque = cafe.estoque ?? 99;

    if (existingIdx >= 0) {
      const novaQtd = Math.min(maxEstoque, current[existingIdx].quantidade + quantidade);
      current[existingIdx] = {
        ...current[existingIdx],
        cafe,
        quantidade: Math.max(1, novaQtd)
      };
    } else {
      current.push({
        cafe,
        quantidade: Math.min(maxEstoque, Math.max(1, quantidade)),
        moagem,
        moagemLabel: opcao.label
      });
    }

    this.items.set(current);
    this.saveToStorage(current);
  }

  updateQuantity(cafeId: number, moagem: TipoMoagem, novaQuantidade: number): void {
    const current = [...this.items()];
    const idx = current.findIndex(i => i.cafe.id === cafeId && i.moagem === moagem);
    if (idx < 0) return;

    if (novaQuantidade <= 0) {
      this.removeItem(cafeId, moagem);
      return;
    }

    const maxEstoque = current[idx].cafe.estoque ?? 99;
    current[idx] = {
      ...current[idx],
      quantidade: Math.min(maxEstoque, novaQuantidade)
    };
    this.items.set(current);
    this.saveToStorage(current);
  }

  removeItem(cafeId: number, moagem: TipoMoagem): void {
    const filtered = this.items().filter(
      i => !(i.cafe.id === cafeId && i.moagem === moagem)
    );
    this.items.set(filtered);
    this.saveToStorage(filtered);
  }

  clear(): void {
    this.items.set([]);
    this.saveToStorage([]);
  }

  private saveToStorage(items: CartItem[]): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(items));
    }
  }

  private loadFromStorage(): CartItem[] {
    if (typeof window !== 'undefined') {
      try {
        const raw = localStorage.getItem(this.STORAGE_KEY);
        return raw ? JSON.parse(raw) : [];
      } catch {
        return [];
      }
    }
    return [];
  }
}
