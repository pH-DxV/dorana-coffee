package br.unitins.topicos1.lgc.usuario.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.usuario.dto.AlterarSenhaDTO;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTO;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTOResponse;
import br.unitins.topicos1.lgc.usuario.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService service;

    @Inject
    JsonWebToken jwt;

    private static final Logger LOG = Logger.getLogger(UsuarioResource.class);

    @GET
    @RolesAllowed({"Administrador"})
    public Response buscarTodos(@QueryParam("nome") String nome) {
        if (nome != null && !nome.isBlank()) {
            LOG.info("BUSCANDO POR NOME: '" + nome + "' [ ADM ACCESS ]");
            List<UsuarioDTOResponse> lista = service.findByNome(nome);
            return Response.ok(lista).build();
        }
        LOG.info("LISTANDO TODOS OS USUARIOS [ ADM ACCESS ]");
        List<UsuarioDTOResponse> lista = service.findAll();
        LOG.info("TOTAL DE USUARIOS RETORNADOS: " + lista.size());
        return Response.ok(lista).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"Administrador", "Usuario"})
    public Response me() {
        Object claimId = jwt.getClaim("id");
        if (claimId == null) {
            throw new ForbiddenException("Usuário não identificado no token.");
        }
        Long id = Long.parseLong(claimId.toString());
        LOG.info("BUSCANDO DADOS DO USUARIO LOGADO: " + id);
        return Response.ok(service.findById(id)).build();
    }

    @PATCH
    @Path("/me/senha")
    @RolesAllowed({"Administrador", "Usuario"})
    public Response alterarMinhaSenha(@Valid AlterarSenhaDTO dto) {
        Object claimId = jwt.getClaim("id");
        if (claimId == null) {
            throw new ForbiddenException("Usuário não identificado no token.");
        }
        Long id = Long.parseLong(claimId.toString());
        LOG.info("ALTERANDO SENHA DO USUARIO LOGADO: " + id);
        service.updateSenha(id, dto.senhaAntiga(), dto.novaSenha());
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO OS DETALHES DO USUARIO: " + id + " POR ID [ ADM ACCESS ]");
        return Response.ok(service.findById(id)).build();
    }

    @POST
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response incluir(@Context UriInfo uriInfo, @Valid UsuarioDTO dto) {
        LOG.info("INICIANDO METODO create [ ADM ACCESS ]");
        UsuarioDTOResponse retorno = service.create(dto);
        Long id = null;
        try {
            id = (Long) retorno.getClass().getMethod("id").invoke(retorno);
        } catch (Exception e) {
            LOG.warn("Não foi possível obter ID do DTO", e);
        }
        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
                    .entity(retorno)
                    .build();
        }
        return Response.status(Status.CREATED).entity(retorno).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid UsuarioDTO dto) {
        LOG.info("INICIANDO METODO update PARA USUARIO: " + id + " [ ADM ACCESS ]");
        UsuarioDTOResponse retorno = service.update(id, dto);
        return Response.ok(retorno).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response apagar(@PathParam("id") Long id) {
        LOG.warn("INICIANDO METODO delete PARA USUARIO: " + id);
        service.delete(id);
        return Response.noContent().build();
    }
}
