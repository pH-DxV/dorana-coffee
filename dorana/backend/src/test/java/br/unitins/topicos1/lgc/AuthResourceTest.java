package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.auth.dto.AuthDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthResourceTest {

    @Test
    @Order(1)
    public void testLoginSucessoAdmin() {
        AuthDTO auth = new AuthDTO("admin", "123456");

        given()
            .contentType(ContentType.JSON)
            .body(auth)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .header("Authorization", notNullValue());
    }

    @Test
    @Order(2)
    public void testLoginSenhaIncorreta() {
        AuthDTO auth = new AuthDTO("admin", "senha_incorreta");

        given()
            .contentType(ContentType.JSON)
            .body(auth)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401);
    }

    @Test
    @Order(3)
    public void testLoginUsuarioInexistente() {
        AuthDTO auth = new AuthDTO("usuario_inexistente", "senha123");

        given()
            .contentType(ContentType.JSON)
            .body(auth)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401);
    }
}
