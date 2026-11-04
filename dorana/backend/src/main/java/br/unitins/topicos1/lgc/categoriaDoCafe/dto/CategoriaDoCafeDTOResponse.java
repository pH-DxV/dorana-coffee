package br.unitins.topicos1.lgc.categoriaDoCafe.dto;

import br.unitins.topicos1.lgc.categoriaDoCafe.model.CategoriaDoCafe;

public record CategoriaDoCafeDTOResponse(

    Long id,
    String nome,
    String descricao

) {
    
    public static CategoriaDoCafeDTOResponse valueOf(CategoriaDoCafe categoria){

        return new CategoriaDoCafeDTOResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao()
        );

    }

}
