package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.estado.dto.EstadoDTO;
import br.unitins.topicos1.lgc.estado.dto.EstadoDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EstadoResourceTest extends AuthTestBase {

    private static Long idEstado;
    private static String tokenAdmin;

    @BeforeAll
    static void setup() {
        tokenAdmin = loginAdmin();
    }

    @Test
    @Order(1)
    public void testFindAll() {
        given()
        .when()
            .get("/estados")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testCreate() {
        EstadoDTO dto = new EstadoDTO("Estado Teste", "ET", 3L);

        EstadoDTOResponse response = given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/estados")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/estados/"))
            .extract()
            .as(EstadoDTOResponse.class);

        idEstado = response.id();
    }

    @Test
    @Order(3)
    public void testFindById() {
        given()
        .when()
            .get("/estados/" + idEstado)
        .then()
            .statusCode(200)
            .body("id", is(idEstado.intValue()));
    }

    @Test
    @Order(4)
    public void testFindByNomeQueryParam() {
        given()
            .queryParam("nome", "Estado Teste")
        .when()
            .get("/estados")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(5)
    public void testUpdate() {
        EstadoDTO dto = new EstadoDTO("Estado Teste Alterado", "EA", 3L);

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .put("/estados/" + idEstado)
        .then()
            .statusCode(200)
            .body("nome", is("Estado Teste Alterado"));
    }

    @Test
    @Order(6)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/estados/" + idEstado)
        .then()
            .statusCode(204);
    }
}
