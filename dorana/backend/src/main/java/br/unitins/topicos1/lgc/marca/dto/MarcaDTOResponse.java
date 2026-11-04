package br.unitins.topicos1.lgc.marca.dto;

import br.unitins.topicos1.lgc.marca.model.Marca;

public record MarcaDTOResponse(

    Long id,
    String nome,
    String descricao

) {

    public static MarcaDTOResponse valueOf(Marca marca){
        return new MarcaDTOResponse(
            marca.getId(),
            marca.getNome(),
            marca.getDescricao()
        );
    }
}
