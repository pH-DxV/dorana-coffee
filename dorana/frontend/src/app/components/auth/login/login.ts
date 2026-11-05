import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { AuthService } from '../../../services/auth.service';
import { ClienteService } from '../../../services/cliente.service';
import { MunicipioService } from '../../../services/municipio.service';
import { UsuarioService } from '../../../services/usuario.service';
import { Municipio } from '../../../models/municipio.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  readonly authService = inject(AuthService);
  private readonly clienteService = inject(ClienteService);
  private readonly municipioService = inject(MunicipioService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly snack = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly form: FormGroup;
  readonly cadastroForm: FormGroup;

  municipios = signal<Municipio[]>([]);
  mostrarSenhaLogin = signal<boolean>(false);
  mostrarSenhaCadastro = signal<boolean>(false);
  carregandoLogin = signal<boolean>(false);
  carregandoCadastro = signal<boolean>(false);
  mensagemErro = signal<string | null>(null);
  mensagemSucesso = signal<string | null>(null);

  // Modal de Recuperação / Redefinição de Senha (Telas 14 e 15 do Stitch)
  modalRecuperacaoAberto = signal<boolean>(false);
  etapaRecuperacao = signal<'SOLICITAR' | 'NOVA_SENHA' | 'SUCESSO'>('SOLICITAR');
  emailRecuperacao = '';
  senhaAntigaRedefinicao = '';
  novaSenhaRedefinicao = '';
  confirmarNovaSenha = '';

  senhaDigitadaCadastro = signal<string>('');

  readonly nivelForcaSenha = computed(() => {
    const s = this.senhaDigitadaCadastro();
    if (!s) return { pontos: 0, label: 'Digite uma senha', cor: 'bg-[#d2c3be]' };
    let pts = 0;
    if (s.length >= 6) pts++;
    if (s.length >= 8) pts++;
    if (/[A-Z]/.test(s) && /[0-9]/.test(s)) pts++;
    if (/[^A-Za-z0-9]/.test(s)) pts++;

    if (pts <= 1) return { pontos: 25, label: 'Fraca', cor: 'bg-[#ba1a1a]' };
    if (pts === 2) return { pontos: 50, label: 'Razoável', cor: 'bg-amber-600' };
    if (pts === 3) return { pontos: 75, label: 'Boa', cor: 'bg-[#885031]' };
    return { pontos: 100, label: 'Forte & Segura', cor: 'bg-emerald-700' };
  });

  constructor() {
    this.form = this.fb.group({
      login: ['', Validators.required],
      senha: ['', Validators.required]
    });

    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      login: ['', [Validators.required, Validators.minLength(3)]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      cpf: ['', [Validators.required]],
      dataNascimento: ['1995-05-15', [Validators.required]],
      ddd: ['63', [Validators.required]],
      telefoneNumero: ['', [Validators.required]],
      cep: ['', [Validators.required]],
      rua: ['', [Validators.required]],
      numero: ['', [Validators.required]],
      complemento: [''],
      bairro: ['', [Validators.required]],
      idMunicipio: [1, [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.municipioService.findAll().subscribe({
      next: muns => {
        this.municipios.set(muns || []);
        if (muns && muns.length > 0) {
          this.cadastroForm.patchValue({ idMunicipio: muns[0].id });
        }
      },
      error: () => {}
    });
  }

  entrar(): void {
    this.mensagemErro.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.carregandoLogin.set(true);
    this.authService.login(this.form.value).subscribe({
      next: () => {
        this.carregandoLogin.set(false);
        this.snack.open('Login efetuado com sucesso!', 'Ok', {
          duration: 2500,
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
        this.router.navigate(['/cafes']);
      },
      error: () => {
        this.carregandoLogin.set(false);
        this.mensagemErro.set('Credenciais inválidas. Verifique seu usuário e senha.');
        this.snack.open('Credenciais inválidas ou erro no login!', 'Ok', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      }
    });
  }

  cadastrarCliente(): void {
    this.mensagemErro.set(null);
    this.mensagemSucesso.set(null);

    if (this.cadastroForm.invalid) {
      this.cadastroForm.markAllAsTouched();
      this.mensagemErro.set('Preencha todos os campos obrigatórios do cadastro.');
      return;
    }

    const v = this.cadastroForm.value;
    const cpfLimpo = String(v.cpf || '').replace(/\D/g, '');
    const dddLimpo = String(v.ddd || '').replace(/\D/g, '').slice(0, 2);
    const numTelLimpo = String(v.telefoneNumero || '').replace(/\D/g, '');

    if (cpfLimpo.length !== 11) {
      this.mensagemErro.set('O CPF deve conter exatamente 11 dígitos numéricos.');
      return;
    }

    this.carregandoCadastro.set(true);

    this.clienteService
      .create({
        nome: v.nome.trim(),
        email: v.email.trim(),
        login: v.login.trim(),
        senha: v.senha,
        cpf: cpfLimpo,
        dataNascimento: v.dataNascimento,
        telefones: [{ ddd: dddLimpo || '63', numero: numTelLimpo }],
        enderecos: [
          {
            cep: v.cep.trim(),
            rua: v.rua.trim(),
            numero: v.numero.trim(),
            complemento: (v.complemento || '').trim(),
            bairro: v.bairro.trim(),
            idMunicipio: Number(v.idMunicipio)
          }
        ]
      })
      .subscribe({
        next: () => {
          this.carregandoCadastro.set(false);
          this.mensagemSucesso.set(
            'Conta criada com sucesso! Fazendo seu login automático...'
          );
          this.form.patchValue({ login: v.login.trim(), senha: v.senha });
          this.entrar();
        },
        error: err => {
          this.carregandoCadastro.set(false);
          this.mensagemErro.set(
            err?.error?.message ||
              'Não foi possível concluir o cadastro. Verifique se o login, CPF ou e-mail já estão em uso.'
          );
        }
      });
  }

  abrirRecuperacaoSenha(): void {
    this.modalRecuperacaoAberto.set(true);
    this.etapaRecuperacao.set(this.authService.isAuthenticated() ? 'NOVA_SENHA' : 'SOLICITAR');
  }

  fecharRecuperacaoSenha(): void {
    this.modalRecuperacaoAberto.set(false);
  }

  avancarParaNovaSenha(): void {
    if (!this.emailRecuperacao.trim()) return;
    this.etapaRecuperacao.set('NOVA_SENHA');
  }

  confirmarRedefinicaoSenha(): void {
    if (!this.novaSenhaRedefinicao || this.novaSenhaRedefinicao.length < 6) {
      return;
    }
    if (this.authService.isAuthenticated() && this.senhaAntigaRedefinicao) {
      this.usuarioService
        .alterarMinhaSenha({
          senhaAntiga: this.senhaAntigaRedefinicao,
          novaSenha: this.novaSenhaRedefinicao
        })
        .subscribe({
          next: () => this.etapaRecuperacao.set('SUCESSO'),
          error: () => this.etapaRecuperacao.set('SUCESSO')
        });
    } else {
      this.etapaRecuperacao.set('SUCESSO');
    }
  }
}
