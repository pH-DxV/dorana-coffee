import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { MarcaService } from './marca.service';
import { Marca } from '../models/marca.model';

describe('MarcaService', () => {
  let service: MarcaService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MarcaService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(MarcaService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should fetch all marcas', () => {
    const mockMarcas: Marca[] = [
      { id: 1, nome: 'Orfeu', descricao: 'Cafés especiais' }
    ];

    service.findAll().subscribe(marcas => {
      expect(marcas.length).toBe(1);
      expect(marcas[0].nome).toBe('Orfeu');
    });

    const req = httpTesting.expectOne('http://localhost:8080/marcas');
    expect(req.request.method).toBe('GET');
    req.flush(mockMarcas);
  });

  it('should create a marca', () => {
    const novaMarca: Marca = { id: 0, nome: 'Baggio', descricao: 'Aromatizados' };

    service.create(novaMarca).subscribe(res => {
      expect(res.id).toBe(2);
      expect(res.nome).toBe('Baggio');
    });

    const req = httpTesting.expectOne('http://localhost:8080/marcas');
    expect(req.request.method).toBe('POST');
    req.flush({ ...novaMarca, id: 2 });
  });

  it('should delete a marca', () => {
    service.delete(1).subscribe(res => {
      expect(res).toBeNull();
    });

    const req = httpTesting.expectOne('http://localhost:8080/marcas/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
