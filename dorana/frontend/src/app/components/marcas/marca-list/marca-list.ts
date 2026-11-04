import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { MarcaService } from '../../../services/marca.service';
import { Marca } from '../../../models/marca.model';

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
  selector: 'app-marca-list',
  styleUrl: './marca-list.css',
  templateUrl: './marca-list.html',
})
export class MarcaListComponent implements OnInit, AfterViewInit {

  displayedColumns: string[] = [
    'numero',
    'nome',
    'descricao',
    'acao'
  ];

  dataSource = new MatTableDataSource<Marca>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private marcaService: MarcaService) {}

  ngOnInit(): void {
    this.marcaService.findAll().subscribe((marcas: Marca[]) => {
      this.dataSource.data = marcas;
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
    this.marcaService.delete(id).subscribe({
      next: () => {
        this.dataSource.data = this.dataSource.data.filter(
          marca => marca.id !== id
        );
      },
      error: (error) => {
        console.error('Erro ao excluir marca:', error);
      }
    });
  }

}
