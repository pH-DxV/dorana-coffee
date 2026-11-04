package br.unitins.topicos1.lgc.marca.resource;

import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.marca.dto.MarcaDTO;
import br.unitins.topicos1.lgc.marca.service.MarcaService;
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

@Path("/marcas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MarcaResource {

    @Inject
    MarcaService marcaService;

    private static final Logger LOG = Logger.getLogger(MarcaResource.class);

    @POST
    @RolesAllowed({"Administrador"})
    public Response create(@Context UriInfo uriInfo, @Valid MarcaDTO dto) {
        LOG.info("INICIANDO METODO create [ ADM ACESS ]");
        var created = marcaService.create(dto);
        Long id = null;
        try { id = (Long) created.getClass().getMethod("id").invoke(created); } catch (Exception e) { LOG.warn("Não foi possível obter ID do DTO", e); }
        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
                    .entity(created).build();
        }
        return Response.status(Status.CREATED).entity(created).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response update(@PathParam("id") Long id, @Valid MarcaDTO dto) {
        LOG.info("INICIANDO METODO update [ ADM ACESS ]");
        return Response.ok(marcaService.update(id, dto)).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response delete(@PathParam("id") Long id) {
        LOG.warn("INICIANDO METODO delete PARA MARCA: " + id + " [ ADM ACESS ]");
        marcaService.delete(id);
        return Response.noContent().build();
    }

    /**
     * Lista marcas com filtro opcional por nome.
     * GET /marcas           → lista todas
     * GET /marcas?nome=...  → filtra por nome
     */
    @GET
    @PermitAll
    public Response findAll(@QueryParam("nome") String nome) {
        if (nome != null && !nome.isBlank()) {
            LOG.info("BUSCANDO POR NOME: '" + nome + "'");
            return Response.ok(marcaService.findByNome(nome)).build();
        }
        LOG.info("LISTANDO TODAS AS MARCAS");
        return Response.ok(marcaService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO DETALHES DA MARCA: " + id + " POR ID");
        return Response.ok(marcaService.findById(id)).build();
    }
}
