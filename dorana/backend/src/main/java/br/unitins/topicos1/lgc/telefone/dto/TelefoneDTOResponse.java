package br.unitins.topicos1.lgc.telefone.dto;

import br.unitins.topicos1.lgc.telefone.model.Telefone;

public record TelefoneDTOResponse(
    Long id,
    String ddd,
    String numero
    // Geralmente não retornamos o ID do usuário aqui, 
    // pois você buscará os telefones *a partir* do usuário.
) {
    public static TelefoneDTOResponse valueOf(Telefone telefone) {
        return new TelefoneDTOResponse(
            telefone.getId(),
            telefone.getDdd(),
            telefone.getNumero()
        );
    }
}