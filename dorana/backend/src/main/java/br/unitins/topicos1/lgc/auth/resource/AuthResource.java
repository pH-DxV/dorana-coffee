package br.unitins.topicos1.lgc.auth.resource;

import java.time.OffsetDateTime;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import br.unitins.topicos1.lgc.auth.dto.AuthDTO;
import br.unitins.topicos1.lgc.auth.dto.AuthDTOResponse;
import br.unitins.topicos1.lgc.exception.Problem;
import br.unitins.topicos1.lgc.hash.service.HashService;
import br.unitins.topicos1.lgc.jwt.service.JwtService;
import br.unitins.topicos1.lgc.usuario.model.Usuario;
import br.unitins.topicos1.lgc.usuario.service.UsuarioService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    HashService hashService;

    @Inject
    JwtService jwtService;

    @Inject
    UsuarioService usuarioService;

    @ConfigProperty(name = "problem.base-url", defaultValue = "http://localhost:8080")
    String baseUrl;

    @Context
    UriInfo uriInfo;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid AuthDTO dto) {
        String hashDaSenha = hashService.getHashSenha(dto.senha());
        Usuario usuario = usuarioService.findByLoginAndSenha(dto.login(), hashDaSenha);
        if (usuario == null) {
            Problem p = new Problem();
            p.type = baseUrl + "/errors/unauthorized";
            p.title = "Não autorizado";
            p.status = Status.UNAUTHORIZED.getStatusCode();
            p.detail = "Credenciais inválidas. Verifique seu login e senha.";
            p.instance = (uriInfo != null ? uriInfo.getRequestUri().getPath() : "/auth/login");
            p.timestamp = OffsetDateTime.now();
            return Response.status(Status.UNAUTHORIZED)
                    .type("application/problem+json")
                    .header("WWW-Authenticate", "Bearer")
                    .entity(p)
                    .build();
        }
        String token = jwtService.generateJwt(usuario);
        AuthDTOResponse responseBody = AuthDTOResponse.valueOf(usuario, token);
        return Response.ok(responseBody)
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
