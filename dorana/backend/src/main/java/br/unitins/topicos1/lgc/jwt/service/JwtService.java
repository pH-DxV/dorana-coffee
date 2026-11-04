package br.unitins.topicos1.lgc.jwt.service;

import br.unitins.topicos1.lgc.usuario.model.Usuario;

public interface JwtService {

    String generateJwt(Usuario usuario);
    
}