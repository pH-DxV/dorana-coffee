import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { UsuarioService } from '../../services/usuario.service';
import { EnderecoService } from '../../services/endereco.service';
import { MunicipioService } from '../../services/municipio.service';
import { PedidoService } from '../../services/pedido.service';
import { PagamentoService } from '../../services/pagamento.service';
import { Usuario } from '../../models/usuario.model';
import { Endereco } from '../../models/endereco.model';
import { Municipio } from '../../models/municipio.model';
import { PedidoDTOResponse } from '../../models/pedido.model';
import { PagamentoDTOResponse } from '../../models/pagamento.model';
import { Cafe, getCafeImage } from '../../models/cafe.model';

export type MetodoPagamentoTipo = 'PIX' | 'CARTAO' | 'BOLETO';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './checkout.html'
})
export class CheckoutComponent implements OnInit {
  readonly authService = inject(AuthService);
  readonly cartService = inject(CartService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly enderecoService = inject(EnderecoService);
  private readonly municipioService = inject(MunicipioService);
  private readonly pedidoService = inject(PedidoService);
  private readonly pagamentoService = inject(PagamentoService);

  usuario = signal<Usuario | null>(null);
  enderecos = signal<Endereco[]>([]);
  municipios = signal<Municipio[]>([]);

  idEnderecoSelecionado = signal<number | null>(null);
  exibirFormNovoEndereco = signal<boolean>(false);

  // Campos de Novo Endereço
  novoCep = '';
  novaRua = '';
  novoNumero = '';
  novoComplemento = '';
  novoBairro = '';
  novoIdMunicipio: number | null = null;

  // Método de Pagamento Escolhido
  metodoPagamento = signal<MetodoPagamentoTipo>('PIX');

  // Campos de Pagamento com Cartão
  nomeTitular = '';
  numeroCartao = '';
  bandeira = 'Mastercard';
  validade = '';
  cvv = '';

  // Estado de Submissão e Confirmação
  processando = signal<boolean>(false);
  erroMensagem = signal<string | null>(null);
  pedidoConcluido = signal<PedidoDTOResponse | null>(null);
  pagamentoConcluido = signal<PagamentoDTOResponse | null>(null);
  copiadoSucesso = signal<boolean>(false);

  ngOnInit(): void {
    this.carregarDadosCheckout();
  }

  carregarDadosCheckout(): void {
    this.municipioService.findAll().subscribe({
      next: muns => {
        this.municipios.set(muns || []);
        if (muns && muns.length > 0 && !this.novoIdMunicipio) {
          this.novoIdMunicipio = muns[0].id;
        }
      },
      error: () => {}
    });

    if (this.authService.isAuthenticated()) {
      this.usuarioService.getMe().subscribe({
        next: u => {
          this.usuario.set(u);
          const ends = u.enderecos || [];
          this.enderecos.set(ends);
          if (ends.length > 0) {
            this.idEnderecoSelecionado.set(ends[0].id);
          } else {
            this.exibirFormNovoEndereco.set(true);
          }
        },
        error: () => {}
      });
    }
  }

  obterImagem(cafe: Cafe, idx: number): string {
    return getCafeImage(cafe, idx);
  }

  salvarNovoEndereco(): void {
    this.erroMensagem.set(null);
    if (!this.novoCep.trim() || !this.novaRua.trim() || !this.novoNumero.trim() || !this.novoBairro.trim() || !this.novoIdMunicipio) {
      this.erroMensagem.set('Preencha todos os campos obrigatórios do novo endereço de entrega.');
      return;
    }

    this.enderecoService
      .create({
        cep: this.novoCep.trim(),
        rua: this.novaRua.trim(),
        numero: this.novoNumero.trim(),
        complemento: this.novoComplemento.trim(),
        bairro: this.novoBairro.trim(),
        idMunicipio: Number(this.novoIdMunicipio)
      })
      .subscribe({
        next: criado => {
          const lista = [...this.enderecos(), criado];
          this.enderecos.set(lista);
          this.idEnderecoSelecionado.set(criado.id);
          this.exibirFormNovoEndereco.set(false);
          this.novoCep = '';
          this.novaRua = '';
          this.novoNumero = '';
          this.novoComplemento = '';
          this.novoBairro = '';
        },
        error: err => {
          this.erroMensagem.set(
            err?.error?.message || 'Não foi possível cadastrar o endereço. Verifique os dados informados.'
          );
        }
      });
  }

  finalizarPedido(): void {
    this.erroMensagem.set(null);

    if (!this.authService.isAuthenticated()) {
      this.erroMensagem.set('Por favor, faça login na sua conta antes de concluir o pedido.');
      return;
    }

    const idEnd = this.idEnderecoSelecionado();
    if (!idEnd) {
      this.erroMensagem.set('Selecione ou cadastre um endereço de entrega para continuar.');
      return;
    }

    if (this.cartService.items().length === 0) {
      this.erroMensagem.set('Sua sacola está vazia.');
      return;
    }

    if (this.metodoPagamento() === 'CARTAO') {
      const numLimpo = this.numeroCartao.replace(/\D/g, '');
      if (!this.nomeTitular.trim() || numLimpo.length < 13 || !this.validade.trim() || this.cvv.trim().length < 3) {
        this.erroMensagem.set('Preencha todos os dados do cartão de crédito corretamente.');
        return;
      }
    }

    // Agrupa itens por idCafe para o contrato PedidoDTO do backend
    const mapaItens = new Map<number, number>();
    for (const item of this.cartService.items()) {
      const atual = mapaItens.get(item.cafe.id) || 0;
      mapaItens.set(item.cafe.id, atual + item.quantidade);
    }

    const itensPayload = Array.from(mapaItens.entries()).map(([idCafe, quantidade]) => ({
      idCafe,
      quantidade
    }));

    this.processando.set(true);

    this.pedidoService
      .create({
        idEnderecoEntrega: idEnd,
        itens: itensPayload
      })
      .subscribe({
        next: pedidoCriado => {
          this.processarPagamentoDoPedido(pedidoCriado);
        },
        error: err => {
          this.processando.set(false);
          this.erroMensagem.set(
            err?.error?.message ||
              'Erro ao registrar o pedido. Verifique a disponibilidade em estoque ou seu endereço.'
          );
        }
      });
  }

  private processarPagamentoDoPedido(pedido: PedidoDTOResponse): void {
    const metodo = this.metodoPagamento();

    if (metodo === 'PIX') {
      this.pagamentoService.pagarPix({ idPedido: pedido.id }).subscribe({
        next: pag => this.concluirFluxo(pedido, pag),
        error: () => this.concluirFluxo(pedido, null)
      });
    } else if (metodo === 'BOLETO') {
      this.pagamentoService.pagarBoleto({ idPedido: pedido.id }).subscribe({
        next: pag => this.concluirFluxo(pedido, pag),
        error: () => this.concluirFluxo(pedido, null)
      });
    } else {
      this.pagamentoService
        .pagarCartao({
          idPedido: pedido.id,
          nomeTitular: this.nomeTitular.trim(),
          numeroCartao: this.numeroCartao.replace(/\D/g, ''),
          bandeira: this.bandeira,
          validade: this.validade.trim(),
          cvv: this.cvv.trim()
        })
        .subscribe({
          next: pag => this.concluirFluxo(pedido, pag),
          error: () => this.concluirFluxo(pedido, null)
        });
    }
  }

  private concluirFluxo(pedido: PedidoDTOResponse, pagamento: PagamentoDTOResponse | null): void {
    this.processando.set(false);
    this.pedidoConcluido.set(pedido);
    this.pagamentoConcluido.set(pagamento);
    this.cartService.clear();
  }

  copiarCodigo(texto?: string): void {
    if (!texto || typeof navigator === 'undefined') return;
    navigator.clipboard?.writeText(texto);
    this.copiadoSucesso.set(true);
    setTimeout(() => this.copiadoSucesso.set(false), 2500);
  }
}
