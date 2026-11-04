package br.unitins.topicos1.lgc.auth.dto;

import java.util.Set;
import java.util.stream.Collectors;

import br.unitins.topicos1.lgc.perfil.model.Perfil;
import br.unitins.topicos1.lgc.usuario.model.Usuario;

public record AuthDTOResponse(
    Long id,
    String nome,
    String login,
    String token,
    Set<String> perfis
) {  
    public static AuthDTOResponse valueOf(Usuario usuario, String token) {
        Set<String> perfilLabels = usuario.getPerfis() != null
            ? usuario.getPerfis().stream().map(Perfil::getLabel).collect(Collectors.toSet())
            : Set.of();

        return new AuthDTOResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getLogin(),
            token,
            perfilLabels
        );
    }
}
