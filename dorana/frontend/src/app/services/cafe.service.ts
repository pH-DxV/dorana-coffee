import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Cafe, CafePayloadDTO } from '../models/cafe.model';

@Injectable({
  providedIn: 'root'
})
export class CafeService {

  private apiUrl = 'http://localhost:8080/cafes';

  constructor(private http: HttpClient) {}

  findAll(nome?: string, minPontuacao?: number, maxPontuacao?: number): Observable<Cafe[]> {
    let params = new HttpParams();
    if (nome && nome.trim()) {
      params = params.set('nome', nome.trim());
    }
    if (minPontuacao != null) {
      params = params.set('minPontuacao', minPontuacao.toString());
    }
    if (maxPontuacao != null) {
      params = params.set('maxPontuacao', maxPontuacao.toString());
    }
    return this.http.get<Cafe[]>(this.apiUrl, { params });
  }

  findById(id: number): Observable<Cafe> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Cafe>(url);
  }

  create(cafe: Cafe | CafePayloadDTO): Observable<Cafe> {
    return this.http.post<Cafe>(this.apiUrl, cafe);
  }

  update(id: number, cafe: Cafe | CafePayloadDTO): Observable<Cafe> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Cafe>(url, cafe);
  }

  delete(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  toPayload(cafe: Cafe, overrideEstoque?: number): CafePayloadDTO {
    return {
      nome: cafe.nome,
      descricao: cafe.descricao || '',
      idMarca: cafe.marca?.id,
      idCategoriaDoCafe: cafe.categoriaDoCafe?.id,
      idNivelDeTorra: cafe.nivelDeTorra?.id,
      idTratamento: cafe.tratamento?.id,
      notasSensoriais: (cafe.notasSensoriais || []).map(n => ({ id: n.id })),
      pontuacaoSCA: cafe.pontuacaoSCA ?? 84,
      preco: cafe.preco,
      peso: cafe.peso,
      estoque: overrideEstoque !== undefined ? overrideEstoque : cafe.estoque
    };
  }
}
