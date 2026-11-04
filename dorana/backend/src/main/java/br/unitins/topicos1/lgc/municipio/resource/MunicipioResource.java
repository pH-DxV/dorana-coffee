package br.unitins.topicos1.lgc.municipio.resource;

import java.util.List;

import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.municipio.dto.MunicipioDTO;
import br.unitins.topicos1.lgc.municipio.dto.MunicipioDTOResponse;
import br.unitins.topicos1.lgc.municipio.service.MunicipioService;
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

@Path("/municipios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MunicipioResource {

    @Inject
    MunicipioService service;

    private static final Logger LOG = Logger.getLogger(MunicipioResource.class);

    /**
     * Lista municípios com filtro opcional por nome.
     * GET /municipios           → lista todos
     * GET /municipios?nome=...  → filtra por nome
     */
    @GET
    @PermitAll
    public Response buscarTodos(@QueryParam("nome") String nome) {
        if (nome != null && !nome.isBlank()) {
            LOG.info("BUSCANDO POR NOME: '" + nome + "'");
            List<MunicipioDTOResponse> lista = service.findByNome(nome);
            return Response.ok(lista).build();
        }
        LOG.info("LISTANDO TODOS OS MUNICIPIOS");
        List<MunicipioDTOResponse> lista = service.findAll();
        LOG.info("TOTAL DE MUNICIPIOS RETORNADOS: " + lista.size());
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response findById(@PathParam("id") Long id) {
        LOG.info("BUSCANDO OS DETALHES DO MUNICIPIO: " + id + " POR ID");
        return Response.ok(service.findById(id)).build();
    }

    @POST
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response incluir(@Context UriInfo uriInfo, @Valid MunicipioDTO dto) {
        LOG.info("INICIANDO METODO create");
        MunicipioDTOResponse retorno = service.create(dto);
        Long id = null;
        try { id = (Long) retorno.getClass().getMethod("id").invoke(retorno); } catch (Exception e) { LOG.warn("Não foi possível obter ID do DTO", e); }
        if (id != null) {
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
                    .entity(retorno).build();
        }
        return Response.status(Status.CREATED).entity(retorno).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid MunicipioDTO dto) {
        LOG.info("INICIANDO METODO update PARA MUNICIPIO: " + id);
        MunicipioDTOResponse retorno = service.update(id, dto);
        return Response.ok(retorno).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Administrador"})
    public Response apagar(@PathParam("id") Long id) {
        LOG.warn("INICIANDO METODO delete PARA MUNICIPIO: " + id);
        service.delete(id);
        return Response.noContent().build();
    }
}
