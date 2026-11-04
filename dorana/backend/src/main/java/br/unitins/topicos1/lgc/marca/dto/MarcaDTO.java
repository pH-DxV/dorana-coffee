package br.unitins.topicos1.lgc.marca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarcaDTO(

    @NotBlank(message = "O nome não pode ser nulo ou vazio.")
    @Size(min = 2, max = 60, message = "O nome da Marca deve ter entre 2 e 60 caracteres.")
    String nome,
    String descricao
    
) {
    
}
