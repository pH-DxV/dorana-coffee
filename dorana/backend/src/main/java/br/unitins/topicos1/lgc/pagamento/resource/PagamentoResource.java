package br.unitins.topicos1.lgc.pagamento.resource;

import org.jboss.logging.Logger;

import br.unitins.topicos1.lgc.pagamento.dto.PagamentoBoletoDTO;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoBoletoDTOResponse;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoCartaoDTO;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoCartaoDTOResponse;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoPixDTO;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoPixDTOResponse;
import br.unitins.topicos1.lgc.pagamento.service.PagamentoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;

@Path("/pagamentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagamentoResource {

    @Inject
    PagamentoService service;

    private static final Logger LOG = Logger.getLogger(PagamentoResource.class);

    @POST
    @Path("/cartao")
    @RolesAllowed({"Usuario", "Administrador"})
    @Transactional
    public Response pagarCartao(@Context UriInfo uriInfo, @Valid PagamentoCartaoDTO dto) {
        LOG.info("INICIANDO METODO pagarCartao");
        PagamentoCartaoDTOResponse response = service.pagarCartao(dto);
        try {
            java.lang.reflect.Method getId = response.getClass().getMethod("id");
            Object id = getId.invoke(response);
            if (id != null) {
                java.net.URI uri = uriInfo.getAbsolutePathBuilder().path(id.toString()).build();
                return Response.created(uri).entity(response).build();
            }
        } catch (Exception e) {
            LOG.warn("Unable to set Location header for pagarCartao", e);
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/pix")
    @RolesAllowed({"Usuario", "Administrador"})
    @Transactional
    public Response pagarPix(@Context UriInfo uriInfo, @Valid PagamentoPixDTO dto) {
        LOG.info("INICIANDO METODO pagarPix");
        PagamentoPixDTOResponse response = service.pagarPix(dto);
        try {
            java.lang.reflect.Method getId = response.getClass().getMethod("id");
            Object id = getId.invoke(response);
            if (id != null) {
                java.net.URI uri = uriInfo.getAbsolutePathBuilder().path(id.toString()).build();
                return Response.created(uri).entity(response).build();
            }
        } catch (Exception e) {
            LOG.warn("Unable to set Location header for pagarPix", e);
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/boleto")
    @RolesAllowed({"Usuario", "Administrador"})
    @Transactional
    public Response pagarBoleto(@Context UriInfo uriInfo, @Valid PagamentoBoletoDTO dto) {
        LOG.info("INICIANDO METODO pagarBoleto");
        PagamentoBoletoDTOResponse response = service.pagarBoleto(dto);
        try {
            java.lang.reflect.Method getId = response.getClass().getMethod("id");
            Object id = getId.invoke(response);
            if (id != null) {
                java.net.URI uri = uriInfo.getAbsolutePathBuilder().path(id.toString()).build();
                return Response.created(uri).entity(response).build();
            }
        } catch (Exception e) {
            LOG.warn("Unable to set Location header for pagarBoleto", e);
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
