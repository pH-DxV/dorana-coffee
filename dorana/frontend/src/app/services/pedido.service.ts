import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PedidoDTO, PedidoDTOResponse } from '../models/pedido.model';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private readonly apiUrl = 'http://localhost:8080/pedidos';

  constructor(private http: HttpClient) {}

  create(dto: PedidoDTO): Observable<PedidoDTOResponse> {
    return this.http.post<PedidoDTOResponse>(this.apiUrl, dto);
  }

  findAll(): Observable<PedidoDTOResponse[]> {
    return this.http.get<PedidoDTOResponse[]>(this.apiUrl);
  }

  findMeusPedidos(): Observable<PedidoDTOResponse[]> {
    return this.http.get<PedidoDTOResponse[]>(`${this.apiUrl}/me`);
  }

  findById(id: number): Observable<PedidoDTOResponse> {
    return this.http.get<PedidoDTOResponse>(`${this.apiUrl}/${id}`);
  }

  cancelar(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/cancelamento`, {});
  }
}
