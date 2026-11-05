import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { CafeService } from '../../../services/cafe.service';
import { MarcaService } from '../../../services/marca.service';
import { CategoriaDoCafeService } from '../../../services/categoria-do-cafe.service';

import {
  Cafe,
  NIVEIS_DE_TORRA_OPCOES,
  NOTAS_SENSORIAIS_OPCOES,
  TRATAMENTOS_OPCOES
} from '../../../models/cafe.model';
import { Marca } from '../../../models/marca.model';
import { CategoriaDoCafe } from '../../../models/categoria-do-cafe.model';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-cafe-form',
  templateUrl: './cafe-form.html',
  styleUrl: './cafe-form.css',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule
  ]
})
export class CafeFormComponent implements OnInit {

  form!: FormGroup;

  marcas: Marca[] = [];
  categorias: CategoriaDoCafe[] = [];

  readonly niveisTorra = NIVEIS_DE_TORRA_OPCOES;
  readonly tratamentos = TRATAMENTOS_OPCOES;
  readonly notasSensoriaisOpcoes = NOTAS_SENSORIAIS_OPCOES;

  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private cafeService: CafeService,
    private marcaService: MarcaService,
    private categoriaService: CategoriaDoCafeService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nome: [
        '',
        [Validators.required, Validators.maxLength(100)]
      ],
      descricao: [
        '',
        [Validators.maxLength(500)]
      ],
      idMarca: [
        null,
        Validators.required
      ],
      idCategoriaDoCafe: [
        null,
        Validators.required
      ],
      idNivelDeTorra: [
        2,
        Validators.required
      ],
      idTratamento: [
        1,
        Validators.required
      ],
      notasSensoriais: [
        []
      ],
      pontuacaoSCA: [
        86,
        Validators.min(0)
      ],
      preco: [
        null,
        [
          Validators.required,
          Validators.min(0)
        ]
      ],
      peso: [
        250,
        [
          Validators.required,
          Validators.min(0)
        ]
      ],
      estoque: [
        10,
        [
          Validators.required,
          Validators.min(0)
        ]
      ]
    });

    this.carregarMarcas();
    this.carregarCategorias();

    const cafe = this.route.snapshot.data['cafe'];

    if (cafe) {
      this.isEdit = true;

      const notasIniciais = (cafe.notasSensoriais ?? []).map((n: any) =>
        typeof n === 'object' && n?.id ? { id: n.id } : n
      );

      this.form.patchValue({
        nome: cafe.nome,
        descricao: cafe.descricao,
        idMarca: cafe.marca?.id,
        idCategoriaDoCafe: cafe.categoriaDoCafe?.id,
        idNivelDeTorra: cafe.nivelDeTorra?.id ?? 2,
        idTratamento: cafe.tratamento?.id ?? 1,
        notasSensoriais: notasIniciais,
        pontuacaoSCA: cafe.pontuacaoSCA,
        preco: cafe.preco,
        peso: cafe.peso,
        estoque: cafe.estoque
      });
    }
  }

  carregarMarcas(): void {
    this.marcaService.findAll().subscribe({
      next: (marcas: Marca[]) => {
        this.marcas = marcas;
      },
      error: (error) => {
        console.error('Erro ao carregar marcas:', error);
      }
    });
  }

  carregarCategorias(): void {
    this.categoriaService.findAll().subscribe({
      next: (categorias: CategoriaDoCafe[]) => {
        this.categorias = categorias;
      },
      error: (error) => {
        console.error('Erro ao carregar categorias:', error);
      }
    });
  }

  notaSelecionada(idNota: number): boolean {
    const lista: any[] = this.form?.get('notasSensoriais')?.value || [];
    return lista.some(item => (typeof item === 'object' ? item?.id === idNota : item === idNota));
  }

  alternarNotaSensorial(idNota: number): void {
    const atual: any[] = [...(this.form.get('notasSensoriais')?.value || [])];
    const idx = atual.findIndex(item =>
      typeof item === 'object' ? item?.id === idNota : item === idNota
    );
    if (idx >= 0) {
      atual.splice(idx, 1);
    } else {
      atual.push({ id: idNota });
    }
    this.form.patchValue({ notasSensoriais: atual });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const cafe: Cafe = this.form.value;

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.cafeService.update(
        Number(id),
        cafe
      ).subscribe({
        next: () => {
          this.router.navigate(['/cafes']);
        },
        error: (error) => {
          console.error('Erro ao atualizar café:', error);
        }
      });
    } else {
      this.cafeService.create(cafe).subscribe({
        next: () => {
          this.router.navigate(['/cafes']);
        },
        error: (error) => {
          console.error('Erro ao cadastrar café:', error);
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/cafes']);
  }
}
