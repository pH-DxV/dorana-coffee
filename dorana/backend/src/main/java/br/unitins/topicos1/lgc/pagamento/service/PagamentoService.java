package br.unitins.topicos1.lgc.pagamento.service;

import br.unitins.topicos1.lgc.pagamento.dto.PagamentoBoletoDTO;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoBoletoDTOResponse;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoCartaoDTO;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoCartaoDTOResponse;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoPixDTO;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoPixDTOResponse;

public interface PagamentoService {
    
    PagamentoCartaoDTOResponse pagarCartao(PagamentoCartaoDTO dto);

    PagamentoPixDTOResponse pagarPix(PagamentoPixDTO dto);

    PagamentoBoletoDTOResponse pagarBoleto(PagamentoBoletoDTO dto);
}