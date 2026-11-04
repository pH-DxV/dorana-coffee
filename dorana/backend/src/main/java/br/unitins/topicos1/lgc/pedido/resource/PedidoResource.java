package br.unitins.topicos1.lgc.pedido.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.pedido.dto.PedidoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTOResponse;
import br.unitins.topicos1.lgc.pedido.service.PedidoService;
import br.unitins.topicos1.lgc.security.service.SecurityService;
import br.unitins.topicos1.lgc.usuario.model.Usuario;
import br.unitins.topicos1.lgc.usuario.repository.UsuarioRepository;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PedidoResource {

    @Inject
    PedidoService service;

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioRepository usuarioRepo;

    @Inject
    SecurityService securityService;

    private static final Logger LOG = Logger.getLogger(PedidoResource.class);

    @POST
    @Transactional
    @RolesAllowed({"Usuario", "Administrador"})
    public Response create(@Context UriInfo uriInfo, @Valid PedidoDTO dto) {
        LOG.info("INICIANDO METODO create");
        PedidoDTOResponse response = service.create(dto);
        LOG.info("PEDIDO CRIADO COM SUCESSO. ID= " + response.id());

        Long id = null;
        try {
            id = (Long) response.getClass().getMethod("id").invoke(response);
        } catch (Exception e) {
            LOG.warn("Não foi possível obter ID do DTO", e);
        }

        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
                    .entity(response)
                    .build();
        }
        return Response.status(Status.CREATED).entity(response).build();
    }

    /**
     * Lista pedidos.
     * Administradores podem listar todos ou filtrar por idUsuario via query param: GET /pedidos?idUsuario=1
     * Usuários comuns listam seus próprios pedidos.
     */
    @GET
    @RolesAllowed({"Administrador", "Usuario"})
    public Response find(@QueryParam("idUsuario") Long idUsuario) {
        if (idUsuario != null) {
            LOG.info("BUSCANDO PEDIDOS POR USUARIO: '" + idUsuario + "'");
            List<PedidoDTOResponse> lista = service.findByUsuario(idUsuario);
            return Response.ok(lista).build();
        }

        // Se for admin e não especificou idUsuario, retorna todos
        Object claimId = jwt.getClaim("id");
        if (claimId != null) {
            Usuario usuarioLogado = usuarioRepo.findById(Long.parseLong(claimId.toString()));
            if (usuarioLogado != null && usuarioLogado.getPerfis().stream().anyMatch(p -> "Administrador".equals(p.getLabel()))) {
                LOG.info("LISTANDO TODOS OS PEDIDOS [ ADM ACCESS ]");
                return Response.ok(service.findAll()).build();
            } else if (usuarioLogado != null) {
                // Usuário comum sem query param recebe seus próprios pedidos
                LOG.info("LISTANDO MEUS PEDIDOS [ USER ID: " + usuarioLogado.getId() + " ]");
                return Response.ok(service.findByUsuario(usuarioLogado.getId())).build();
            }
        }

        throw new ForbiddenException("Não autorizado para listar todos os pedidos.");
    }

    @GET
    @Path("/me")
    @RolesAllowed({"Usuario", "Administrador"})
    public Response findMeusPedidos() {
        Object claimId = jwt.getClaim("id");
        if (claimId == null) {
            throw new ForbiddenException("Usuário não identificado no token.");
        }
        Long idUsuario = Long.parseLong(claimId.toString());
        LOG.info("BUSCANDO MEUS PEDIDOS: " + idUsuario);
        return Response.ok(service.findByUsuario(idUsuario)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"Administrador", "Usuario"})
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO OS DETALHES DO PEDIDO: " + id + " POR ID");
        return Response.ok(service.findById(id)).build();
    }

    @POST
    @Path("/{id}/cancelamento")
    @Transactional
    @RolesAllowed({"Administrador", "Usuario"})
    public Response cancelar(@PathParam("id") Long id) {
        LOG.info("CANCELANDO PEDIDO ID: " + id);
        PedidoDTOResponse response = service.cancelar(id);
        return Response.ok(response).build();
    }
}
