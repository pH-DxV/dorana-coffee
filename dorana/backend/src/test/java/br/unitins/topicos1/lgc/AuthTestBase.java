package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;

import br.unitins.topicos1.lgc.auth.dto.AuthDTO;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class AuthTestBase {

    private static String tokenAdmin;
    private static String tokenCliente;

    public static synchronized String loginAdmin() {
        if (tokenAdmin == null) {
            tokenAdmin = extrairToken("admin", "123456");
        }
        return tokenAdmin;
    }

    public static synchronized String loginCliente() {
        if (tokenCliente == null) {
            tokenCliente = extrairToken("cliente", "123456");
        }
        return tokenCliente;
    }

    public static void resetTokens() {
        tokenAdmin = null;
        tokenCliente = null;
    }

    private static String extrairToken(String login, String senha) {
        AuthDTO auth = new AuthDTO(login, senha);

        Response response = given()
            .port(8081)
            .contentType(ContentType.JSON)
            .body(auth)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response();

        String token = response.path("token");
        if (token == null || token.isBlank()) {
            String header = response.header("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }
        }
        return token;
    }
}
