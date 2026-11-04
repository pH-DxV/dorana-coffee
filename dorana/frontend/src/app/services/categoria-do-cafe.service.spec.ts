import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { CategoriaDoCafeService } from './categoria-do-cafe.service';
import { CategoriaDoCafe } from '../models/categoria-do-cafe.model';

describe('CategoriaDoCafeService', () => {
  let service: CategoriaDoCafeService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CategoriaDoCafeService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CategoriaDoCafeService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should fetch all categorias', () => {
    const mockCategorias: CategoriaDoCafe[] = [
      { id: 1, nome: 'Especial', descricao: 'Grãos selecionados' }
    ];

    service.findAll().subscribe(categorias => {
      expect(categorias.length).toBe(1);
      expect(categorias[0].nome).toBe('Especial');
    });

    const req = httpTesting.expectOne('http://localhost:8080/categorias');
    expect(req.request.method).toBe('GET');
    req.flush(mockCategorias);
  });

  it('should create a categoria', () => {
    const novaCat: CategoriaDoCafe = { id: 0, nome: 'Gourmet', descricao: 'Cafés gourmet' };

    service.create(novaCat).subscribe(res => {
      expect(res.id).toBe(2);
      expect(res.nome).toBe('Gourmet');
    });

    const req = httpTesting.expectOne('http://localhost:8080/categorias');
    expect(req.request.method).toBe('POST');
    req.flush({ ...novaCat, id: 2 });
  });
});
