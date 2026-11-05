export interface PagamentoPixDTO {
  idPedido: number;
}

export interface PagamentoBoletoDTO {
  idPedido: number;
}

export interface PagamentoCartaoDTO {
  idPedido: number;
  nomeTitular: string;
  numeroCartao: string;
  bandeira: string;
  validade: string;
  cvv: string;
}

export interface PagamentoDTOResponse {
  id: number;
  valor: number;
  confirmado: boolean;
  chavePix?: string;
  dataExpiracao?: string;
  codigoBarras?: string;
  dataVencimento?: string;
  nomeTitular?: string;
  numeroCartao?: string;
  bandeira?: string;
}
