import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CategoriaDoCafeService } from '../../../services/categoria-do-cafe.service';
import { CategoriaDoCafe } from '../../../models/categoria-do-cafe.model';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';

@Component({
  imports: [
    MatTableModule,
    MatInputModule,
    MatFormFieldModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    RouterLink
  ],
  selector: 'app-categoria-list',
  styleUrl: './categoria-list.css',
  templateUrl: './categoria-list.html',
})
export class CategoriaListComponent implements OnInit, AfterViewInit {

  displayedColumns: string[] = [
    'numero',
    'nome',
    'descricao',
    'acao'
  ];

  dataSource = new MatTableDataSource<CategoriaDoCafe>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private categoriaService: CategoriaDoCafeService) {}

  ngOnInit(): void {
    this.categoriaService.findAll().subscribe((categorias: CategoriaDoCafe[]) => {
      this.dataSource.data = categorias;
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  excluir(id: number): void {
    this.categoriaService.delete(id).subscribe({
      next: () => {
        this.dataSource.data = this.dataSource.data.filter(
          categoria => categoria.id !== id
        );
      },
      error: (error) => {
        console.error('Erro ao excluir categoria:', error);
      }
    });
  }

}
