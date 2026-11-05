import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { AlterarSenhaDTO } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private readonly apiUrl: string = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient) {}

  findAll(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  findById(id: number): Observable<Usuario> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Usuario>(url);
  }

  findByNome(nome: string): Observable<Usuario[]> {
    const url = `${this.apiUrl}?nome=${encodeURIComponent(nome)}`;
    return this.http.get<Usuario[]>(url);
  }

  getMe(): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/me`);
  }

  alterarMinhaSenha(dto: AlterarSenhaDTO): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/me/senha`, dto);
  }

  create(usuario: Usuario | any): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }

  update(id: number, usuario: Usuario | any): Observable<Usuario> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Usuario>(url, usuario);
  }

  delete(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }
}
