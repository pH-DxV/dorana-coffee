package br.unitins.topicos1.lgc.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
    @NotBlank(message = "A senha antiga deve ser informada.")
    String senhaAntiga,

    @NotBlank(message = "A nova senha deve ser informada.")
    @Size(min = 6, message = "A nova senha deve possuir no mínimo 6 caracteres.")
    String novaSenha
) {}
