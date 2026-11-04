package br.unitins.topicos1.lgc.frete.service;

import java.math.BigDecimal;

import br.unitins.topicos1.lgc.endereco.model.Endereco;

public interface FreteService {
    
    // Calcula o frete baseando-se no CEP (Estado)
    BigDecimal calcularFrete(Endereco enderecoEntrega);
    
}