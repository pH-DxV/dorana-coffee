import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  PagamentoBoletoDTO,
  PagamentoCartaoDTO,
  PagamentoDTOResponse,
  PagamentoPixDTO
} from '../models/pagamento.model';

@Injectable({
  providedIn: 'root'
})
export class PagamentoService {
  private readonly apiUrl = 'http://localhost:8080/pagamentos';

  constructor(private http: HttpClient) {}

  pagarPix(dto: PagamentoPixDTO): Observable<PagamentoDTOResponse> {
    return this.http.post<PagamentoDTOResponse>(`${this.apiUrl}/pix`, dto);
  }

  pagarCartao(dto: PagamentoCartaoDTO): Observable<PagamentoDTOResponse> {
    return this.http.post<PagamentoDTOResponse>(`${this.apiUrl}/cartao`, dto);
  }

  pagarBoleto(dto: PagamentoBoletoDTO): Observable<PagamentoDTOResponse> {
    return this.http.post<PagamentoDTOResponse>(`${this.apiUrl}/boleto`, dto);
  }
}
