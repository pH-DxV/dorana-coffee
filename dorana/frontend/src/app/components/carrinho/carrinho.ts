import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Cafe, getCafeImage } from '../../models/cafe.model';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './carrinho.html'
})
export class CarrinhoComponent {
  readonly cartService = inject(CartService);

  cepSimulacao = '';
  prazoSimulado = signal<string | null>(null);

  obterImagem(cafe: Cafe, index: number): string {
    return getCafeImage(cafe, index);
  }

  simularFrete(): void {
    const cepLimpo = this.cepSimulacao.replace(/\D/g, '');
    if (cepLimpo.length < 8) {
      this.prazoSimulado.set('Informe um CEP válido com 8 dígitos.');
      return;
    }
    this.prazoSimulado.set(
      'Entrega Expressa Torra Fresca: 2 a 4 dias úteis após a torra.'
    );
  }
}
