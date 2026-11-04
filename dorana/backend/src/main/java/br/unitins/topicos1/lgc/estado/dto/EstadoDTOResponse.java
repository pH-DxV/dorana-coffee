package br.unitins.topicos1.lgc.estado.dto;

import br.unitins.topicos1.lgc.estado.model.Estado;
import br.unitins.topicos1.lgc.regiao.model.Regiao;

public record EstadoDTOResponse(
    
    Long id,
    String nome,
    String sigla,
    Regiao regiao
    
) {  
    
    public static EstadoDTOResponse valueOf(Estado estado) {
        return new EstadoDTOResponse(
            estado.getId(),
            estado.getNome(),
            estado.getSigla(),
            estado.getRegiao()
        );

    }

}