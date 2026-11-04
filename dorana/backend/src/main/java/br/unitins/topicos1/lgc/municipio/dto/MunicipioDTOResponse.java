package br.unitins.topicos1.lgc.municipio.dto;

import br.unitins.topicos1.lgc.estado.dto.EstadoDTOResponse;
import br.unitins.topicos1.lgc.municipio.model.Municipio;

public record MunicipioDTOResponse(
    Long id,
    String nome,
    EstadoDTOResponse estado // Retorna o DTO do estado, não a entidade
) {
    public static MunicipioDTOResponse valueOf(Municipio municipio) {
        return new MunicipioDTOResponse(
            municipio.getId(),
            municipio.getNome(),
            EstadoDTOResponse.valueOf(municipio.getEstado()) // Converte o estado
        );
    }
}