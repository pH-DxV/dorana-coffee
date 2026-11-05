import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Endereco } from '../models/endereco.model';
import { EnderecoPayloadDTO } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class EnderecoService {
  private readonly apiUrl = 'http://localhost:8080/enderecos';

  constructor(private http: HttpClient) {}

  findAll(): Observable<Endereco[]> {
    return this.http.get<Endereco[]>(this.apiUrl);
  }

  findById(id: number): Observable<Endereco> {
    return this.http.get<Endereco>(`${this.apiUrl}/${id}`);
  }

  create(dto: EnderecoPayloadDTO): Observable<Endereco> {
    return this.http.post<Endereco>(this.apiUrl, dto);
  }

  update(id: number, dto: EnderecoPayloadDTO): Observable<Endereco> {
    return this.http.put<Endereco>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
