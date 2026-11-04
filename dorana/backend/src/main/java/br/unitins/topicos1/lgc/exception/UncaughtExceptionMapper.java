package br.unitins.topicos1.lgc.exception;

import java.time.OffsetDateTime;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UncaughtExceptionMapper.class);

    @ConfigProperty(name = "problem.base-url", defaultValue = "http://localhost:8080")
    String baseUrl;
    
    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable t) {
        LOG.errorf(t, "Erro inesperado em %s", (uriInfo != null ? uriInfo.getRequestUri() : "<no-uri>"));

        Problem p = new Problem();
        p.type = baseUrl + "/errors/unexpected-error";
        p.title = "Erro inesperado";
        p.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        p.detail = "Ocorreu um erro interno no servidor. Tente novamente mais tarde.";
        p.instance = (uriInfo != null ? uriInfo.getRequestUri().getPath() : null);
        p.timestamp = OffsetDateTime.now();

        return Response.status(p.status)
                .type("application/problem+json")
                .entity(p)
                .build();
    }
}