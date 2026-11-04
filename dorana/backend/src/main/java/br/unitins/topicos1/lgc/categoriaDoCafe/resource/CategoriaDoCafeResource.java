package br.unitins.topicos1.lgc.categoriaDoCafe.resource;

import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.categoriaDoCafe.dto.CategoriaDoCafeDTO;
import br.unitins.topicos1.lgc.categoriaDoCafe.service.CategoriaDoCafeService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaDoCafeResource {

    @Inject
    CategoriaDoCafeService service;

    private static final Logger LOG = Logger.getLogger(CategoriaDoCafeResource.class);

    @POST
    @RolesAllowed({"Administrador"})
    public Response create(@Context UriInfo uriInfo, @Valid CategoriaDoCafeDTO dto) {
        LOG.info("INICIANDO METODO create [ ADM ACESS ]");
        var created = service.create(dto);
        Long id = null;
        try {
            id = (Long) created.getClass().getMethod("id").invoke(created);
        } catch (Exception e) {
            LOG.warn("Não foi possível obter ID do DTO", e);
        }
        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
                    .entity(created)
                    .build();
        }
        return Response.status(Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response update(@PathParam("id") Long id, @Valid CategoriaDoCafeDTO dto) {
        LOG.info("INICIANDO METODO update PARA CATEGORIA: " + id + " [ ADM ACESS ]");
        return Response.ok(service.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response delete(@PathParam("id") Long id) {
        LOG.warn("INICIANDO METODO delete PARA CATEGORIA: " + id + " [ ADM ACESS ]");
        service.delete(id);
        return Response.noContent().build();
    }

    /**
     * Lista categorias com filtro opcional por nome.
     * GET /categorias           → lista todas
     * GET /categorias?nome=...  → filtra por nome
     */
    @GET
    @PermitAll
    public Response findAll(@QueryParam("nome") String nome) {
        if (nome != null && !nome.isBlank()) {
            LOG.info("BUSCANDO AS CATEGORIAS POR NOME: '" + nome + "'");
            return Response.ok(service.findByNome(nome)).build();
        }
        LOG.info("LISTANDO TODAS AS CATEGORIAS DE CAFE");
        return Response.ok(service.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO OS DETALHES DA CATEGORIA: " + id);
        return Response.ok(service.findById(id)).build();
    }
}
