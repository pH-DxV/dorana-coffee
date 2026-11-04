package br.unitins.topicos1.lgc.administrador.dto;

import java.time.LocalDate;

import br.unitins.topicos1.lgc.administrador.model.Administrador;

public record AdministradorDTOResponse(
    Long id,
    String nome,
    String email,
    String login,
    String cpf,
    LocalDate dataNascimento,
    String cargo
) {
    public static AdministradorDTOResponse valueOf(Administrador admin) {
        return new AdministradorDTOResponse(
            admin.getId(),
            admin.getNome(),
            admin.getEmail(),
            admin.getLogin(),
            admin.getCpf(),
            admin.getDataNascimento(),
            admin.getCargo()
        );
    }
}
