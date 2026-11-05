import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { PedidoService } from '../../services/pedido.service';
import { EnderecoService } from '../../services/endereco.service';
import { TelefoneService } from '../../services/telefone.service';
import { MunicipioService } from '../../services/municipio.service';
import { Usuario } from '../../models/usuario.model';
import { PedidoDTOResponse } from '../../models/pedido.model';
import { Municipio } from '../../models/municipio.model';

export type AbaMinhaConta = 'PEDIDOS' | 'DADOS' | 'ENDERECOS';

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './minha-conta.html'
})
export class MinhaContaComponent implements OnInit {
  readonly authService = inject(AuthService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly pedidoService = inject(PedidoService);
  private readonly enderecoService = inject(EnderecoService);
  private readonly telefoneService = inject(TelefoneService);
  private readonly municipioService = inject(MunicipioService);
  private readonly router = inject(Router);

  abaAtiva = signal<AbaMinhaConta>('PEDIDOS');
  usuario = signal<Usuario | null>(null);
  pedidos = signal<PedidoDTOResponse[]>([]);
  municipios = signal<Municipio[]>([]);
  carregando = signal<boolean>(true);
  feedbackMsg = signal<string | null>(null);
  erroMsg = signal<string | null>(null);

  // Novo Telefone
  novoDdd = '63';
  novoNumeroTelefone = '';

  // Alterar Senha (PATCH /usuarios/me/senha)
  senhaAntiga = '';
  novaSenha = '';

  // Novo Endereço
  novoCep = '';
  novaRua = '';
  novoNumero = '';
  novoComplemento = '';
  novoBairro = '';
  novoIdMunicipio: number | null = null;

  ngOnInit(): void {
    this.carregarDadosCliente();
  }

  carregarDadosCliente(): void {
    if (!this.authService.isAuthenticated()) {
      this.carregando.set(false);
      return;
    }

    this.carregando.set(true);

    this.usuarioService.getMe().subscribe({
      next: u => {
        this.usuario.set(u);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false)
    });

    this.pedidoService.findMeusPedidos().subscribe({
      next: p => this.pedidos.set(p || []),
      error: () => {}
    });

    this.municipioService.findAll().subscribe({
      next: m => {
        this.municipios.set(m || []);
        if (m && m.length > 0 && !this.novoIdMunicipio) {
          this.novoIdMunicipio = m[0].id;
        }
      },
      error: () => {}
    });
  }

  podeCancelarPedido(pedido: PedidoDTOResponse): boolean {
    const idStatus = pedido.status?.id;
    const label = (pedido.status?.label || '').toLowerCase();
    return (
      idStatus === 1 ||
      idStatus === 2 ||
      label.includes('aguardando') ||
      label.includes('confirmado')
    );
  }

  etapaStatus(pedido: PedidoDTOResponse): number {
    const id = pedido.status?.id;
    if (id === 6) return -1; // Cancelado
    return id || 1;
  }

  cancelarPedido(idPedido: number): void {
    this.erroMsg.set(null);
    this.pedidoService.cancelar(idPedido).subscribe({
      next: () => {
        this.mostrarFeedback(`Pedido #${idPedido} cancelado com sucesso e estoque estornado.`);
        this.pedidoService.findMeusPedidos().subscribe(p => this.pedidos.set(p || []));
      },
      error: err => {
        this.erroMsg.set(
          err?.error?.message || 'Não foi possível cancelar este pedido no status atual.'
        );
      }
    });
  }

  adicionarTelefone(): void {
    this.erroMsg.set(null);
    if (!this.novoDdd.trim() || !this.novoNumeroTelefone.trim()) return;
    this.telefoneService
      .create({
        ddd: this.novoDdd.trim(),
        numero: this.novoNumeroTelefone.replace(/\D/g, '')
      })
      .subscribe({
        next: () => {
          this.novoNumeroTelefone = '';
          this.mostrarFeedback('Telefone adicionado com sucesso!');
          this.usuarioService.getMe().subscribe(u => this.usuario.set(u));
        },
        error: () => this.erroMsg.set('Erro ao cadastrar telefone.')
      });
  }

  removerTelefone(id: number): void {
    this.telefoneService.delete(id).subscribe({
      next: () => {
        this.mostrarFeedback('Telefone removido.');
        this.usuarioService.getMe().subscribe(u => this.usuario.set(u));
      },
      error: () => {}
    });
  }

  salvarNovaSenha(): void {
    this.erroMsg.set(null);
    if (!this.senhaAntiga || this.novaSenha.length < 6) {
      this.erroMsg.set('Informe a senha atual e uma nova senha com mínimo de 6 caracteres.');
      return;
    }
    this.usuarioService
      .alterarMinhaSenha({
        senhaAntiga: this.senhaAntiga,
        novaSenha: this.novaSenha
      })
      .subscribe({
        next: () => {
          this.senhaAntiga = '';
          this.novaSenha = '';
          this.mostrarFeedback('Sua senha foi atualizada com sucesso!');
        },
        error: err => {
          this.erroMsg.set(
            err?.error?.message || 'Senha atual incorreta ou erro ao atualizar senha.'
          );
        }
      });
  }

  adicionarEndereco(): void {
    this.erroMsg.set(null);
    if (!this.novoCep.trim() || !this.novaRua.trim() || !this.novoNumero.trim() || !this.novoBairro.trim() || !this.novoIdMunicipio) {
      this.erroMsg.set('Preencha todos os campos obrigatórios do endereço.');
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
        next: () => {
          this.novoCep = '';
          this.novaRua = '';
          this.novoNumero = '';
          this.novoComplemento = '';
          this.novoBairro = '';
          this.mostrarFeedback('Novo endereço cadastrado com sucesso!');
          this.usuarioService.getMe().subscribe(u => this.usuario.set(u));
        },
        error: () => this.erroMsg.set('Erro ao salvar endereço.')
      });
  }

  removerEndereco(id: number): void {
    this.enderecoService.delete(id).subscribe({
      next: () => {
        this.mostrarFeedback('Endereço removido.');
        this.usuarioService.getMe().subscribe(u => this.usuario.set(u));
      },
      error: () => this.erroMsg.set('Este endereço está vinculado a um pedido e não pode ser removido.')
    });
  }

  sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private mostrarFeedback(msg: string): void {
    this.feedbackMsg.set(msg);
    setTimeout(() => this.feedbackMsg.set(null), 3500);
  }
}
