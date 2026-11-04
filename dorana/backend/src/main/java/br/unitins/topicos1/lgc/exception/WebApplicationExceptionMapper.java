package br.unitins.topicos1.lgc.exception;

import java.time.OffsetDateTime;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Context
    UriInfo uriInfo;

    @ConfigProperty(name = "problem.base-url", defaultValue = "http://localhost:8080")
    String baseUrl;

    @Override
    public Response toResponse(WebApplicationException e) {
        int statusCode = e.getResponse() != null ? e.getResponse().getStatus() : 500;
        String title = switch (statusCode) {
            case 400 -> "Requisição inválida";
            case 401 -> "Não autenticado";
            case 403 -> "Acesso negado";
            case 404 -> "Recurso não encontrado";
            case 409 -> "Conflito de dados";
            case 422 -> "Entidade não processável";
            default -> "Erro na requisição";
        };

        Problem p = new Problem();
        p.type = baseUrl + "/errors/" + statusCode;
        p.title = title;
        p.status = statusCode;
        p.detail = e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage() : title;
        p.instance = uriInfo != null ? uriInfo.getRequestUri().getPath() : null;
        p.timestamp = OffsetDateTime.now();

        return Response.status(statusCode)
                .type("application/problem+json")
                .entity(p)
                .build();
    }
}
