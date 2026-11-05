import { Endereco } from './endereco.model';
import { PagamentoDTOResponse } from './pagamento.model';

export interface ItemPedidoDTO {
  idCafe: number;
  quantidade: number;
}

export interface PedidoDTO {
  idEnderecoEntrega: number;
  itens: ItemPedidoDTO[];
}

export interface ItemPedidoDTOResponse {
  idCafe: number;
  nomeCafe: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
}

export interface PedidoStatusDTO {
  id: number;
  label: string;
}

export interface PedidoDTOResponse {
  id: number;
  dataHora: string;
  totalPedido: number;
  valorFrete: number;
  usuario?: {
    id: number;
    nome?: string;
    login?: string;
  };
  enderecoEntrega?: Endereco;
  itens: ItemPedidoDTOResponse[];
  pagamento?: PagamentoDTOResponse;
  status?: PedidoStatusDTO;
}
