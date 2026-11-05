import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Telefone } from '../models/telefone.model';
import { TelefonePayloadDTO } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class TelefoneService {
  private readonly apiUrl = 'http://localhost:8080/telefones';

  constructor(private http: HttpClient) {}

  findByUsuario(idUsuario: number): Observable<Telefone[]> {
    return this.http.get<Telefone[]>(`${this.apiUrl}/usuario/${idUsuario}`);
  }

  create(dto: TelefonePayloadDTO): Observable<Telefone> {
    return this.http.post<Telefone>(this.apiUrl, dto);
  }

  update(id: number, dto: TelefonePayloadDTO): Observable<Telefone> {
    return this.http.put<Telefone>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
