import { Location } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { MarcaService } from '../../../services/marca.service';

@Component({
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatSnackBarModule
  ],
  selector: 'app-marca-form',
  styleUrl: './marca-form.css',
  templateUrl: './marca-form.html',
})
export class MarcaFormComponent implements OnInit {

  readonly form: FormGroup;
  private readonly location = inject(Location);

  constructor(
    private fb: FormBuilder,
    private marcaService: MarcaService,
    private activatedRoute: ActivatedRoute,
    private snack: MatSnackBar,
    private router: Router
  ) {
    this.form = this.fb.group({
      id: [null],
      nome: ['', Validators.required],
      descricao: ['']
    });
  }

  ngOnInit(): void {
    const marca = this.activatedRoute.snapshot.data['marca'];
    if (marca) {
      this.form.patchValue(marca);
    }
  }

  salvar(): void {
    if (this.form.invalid) {
      this.exibirMensagem('Preencha os campos obrigatórios!');
      return;
    }

    const marca = this.form.value;
    const resultado = marca.id
      ? this.marcaService.update(marca.id, marca)
      : this.marcaService.create(marca);

    resultado.subscribe({
      next: () => {
        this.exibirMensagem('Marca salva com sucesso!');
        this.router.navigate(['/marcas']);
      },
      error: (error) => {
        this.exibirMensagem('Erro ao salvar marca!');
        console.error('Erro ao salvar marca:', error);
      }
    });
  }

  excluir(): void {
    const marca = this.form.value;
    if (marca.id) {
      this.marcaService.delete(marca.id).subscribe({
        next: () => {
          this.exibirMensagem('Marca excluída com sucesso!');
          this.router.navigate(['/marcas']);
        },
        error: (error) => {
          this.exibirMensagem('Erro ao excluir marca!');
          console.error('Erro ao excluir marca:', error);
        }
      });
    }
  }

  exibirMensagem(mensagem: string): void {
    this.snack.open(mensagem, 'Ok', {
      duration: 2500,
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  voltar(): void {
    this.location.back();
  }
}
