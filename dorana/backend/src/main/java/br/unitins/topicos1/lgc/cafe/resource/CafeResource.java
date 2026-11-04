package br.unitins.topicos1.lgc.cafe.resource;

import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.cafe.dto.CafeDTO;
import br.unitins.topicos1.lgc.cafe.service.CafeService;
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

@Path("/cafes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CafeResource {

    @Inject
    CafeService cafeService;

    private static final Logger LOG = Logger.getLogger(CafeResource.class);

    @POST
    @RolesAllowed({"Administrador"})
    public Response create(@Context UriInfo uriInfo, @Valid CafeDTO dto) {
        LOG.info("INICIANDO METODO create");
        var createdDto = cafeService.create(dto);
        Long id = null;
        try { id = (Long) createdDto.getClass().getMethod("id").invoke(createdDto); } catch (Exception e) { LOG.warn("Não foi possível obter ID do DTO", e); }
        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build()).entity(createdDto).build();
        }
        return Response.status(Status.CREATED).entity(createdDto).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response update(@PathParam("id") Long id, @Valid CafeDTO dto) {
        LOG.info("INICIANDO METODO update PARA CAFE " + id);
        return Response.ok(cafeService.update(id, dto)).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed({"Administrador"})
    public Response delete(@PathParam("id") Long id) {
        LOG.warn("INICIANDO METODO delete PARA CAFE " + id + " [ ADM ACESS ]");
        cafeService.delete(id);
        return Response.noContent().build();
    }

    /**
     * Lista cafés com filtros opcionais por query params.
     * GET /cafes                                  → lista todos
     * GET /cafes?nome=Bourbon                     → filtra por nome
     * GET /cafes?minPontuacao=80&maxPontuacao=90   → filtra por faixa de pontuação SCA
     */
    @GET
    @PermitAll
    public Response findAll(
            @QueryParam("nome") String nome,
            @QueryParam("minPontuacao") Integer minSCA,
            @QueryParam("maxPontuacao") Integer maxSCA) {

        if (nome != null && !nome.isBlank()) {
            LOG.info("BUSCANDO POR NOME: '" + nome + "'");
            return Response.ok(cafeService.findByNome(nome)).build();
        }
        if (minSCA != null || maxSCA != null) {
            LOG.info("LISTANDO CAFES POR PONTUACAO [min=" + minSCA + ", max=" + maxSCA + "]");
            return Response.ok(cafeService.findByPontuacao(minSCA, maxSCA)).build();
        }

        LOG.info("LISTANDO TODOS OS CAFES");
        return Response.ok(cafeService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO OS DETALHES DO CAFE " + id + " POR ID");
        return Response.ok(cafeService.findById(id)).build();
    }
}
