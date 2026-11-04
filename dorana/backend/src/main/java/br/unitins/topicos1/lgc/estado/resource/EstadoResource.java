package br.unitins.topicos1.lgc.estado.resource;

import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.estado.dto.EstadoDTO;
import br.unitins.topicos1.lgc.estado.dto.EstadoDTOResponse;
import br.unitins.topicos1.lgc.estado.service.EstadoService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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

@Path("/estados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstadoResource {

    @Inject
    EstadoService service;

    private static final Logger LOG = Logger.getLogger(EstadoResource.class);

    /**
     * Lista estados com filtro opcional por nome.
     * GET /estados           → lista todos
     * GET /estados?nome=...  → filtra por nome
     */
    @GET
    @PermitAll
    public Response buscarTodos(@QueryParam("nome") String nome) {
        if (nome != null && !nome.isBlank()) {
            LOG.info("BUSCANDO ESTADO POR NOME: " + nome);
            return Response.ok(service.findByNome(nome)).build();
        }
        LOG.info("LISTANDO TODOS OS ESTADOS");
        return Response.ok(service.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO ESTADO POR ID: " + id);
        return Response.ok(service.findById(id)).build();
    }

    @POST
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response incluir(@Context UriInfo uriInfo, @Valid EstadoDTO dto) {
        LOG.info("INICIANDO METODO create [ ADM ACESS ]");
        EstadoDTOResponse response = service.create(dto);
        Long id = null;
        try { id = (Long) response.getClass().getMethod("id").invoke(response); } catch (Exception e) { LOG.warn("Não foi possível obter ID do DTO", e); }
        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build()).entity(response).build();
        }
        return Response.status(Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid EstadoDTO dto) {
        LOG.info("INICIANDO METODO update PARA O ESTADO: " + id + " [ ADM ACESS ]");
        EstadoDTOResponse response = service.update(id, dto);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response apagar(@PathParam("id") Long id) {
        LOG.warn("INICIANDO METODO delete PARA ESTADO: " + id + " [ ADM ACESS ]");
        service.delete(id);
        return Response.noContent().build();
    }
}
