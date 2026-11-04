package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.marca.dto.MarcaDTO;
import br.unitins.topicos1.lgc.marca.dto.MarcaDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarcaResourceTest extends AuthTestBase {

    private static Long idMarca;
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
            .get("/marcas")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testCreate() {
        MarcaDTO dto = new MarcaDTO("Marca Teste", "Descricao Teste");

        MarcaDTOResponse response = given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/marcas")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/marcas/"))
            .body("id", notNullValue())
            .body("nome", is("Marca Teste"))
            .extract().as(MarcaDTOResponse.class);

        idMarca = response.id();
    }

    @Test
    @Order(3)
    public void testUpdate() {
        MarcaDTO dto = new MarcaDTO("Marca Teste (Atualizada)", "Descricao Atualizada");

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .put("/marcas/" + idMarca)
        .then()
            .statusCode(200)
            .body("id", is(idMarca.intValue()))
            .body("nome", is("Marca Teste (Atualizada)"));
    }

    @Test
    @Order(4)
    public void testFindById() {
        given()
        .when()
            .get("/marcas/" + idMarca)
        .then()
            .statusCode(200)
            .body("id", is(idMarca.intValue()));
    }

    @Test
    @Order(5)
    public void testFindByNomeQueryParam() {
        given()
            .queryParam("nome", "Marca Teste")
        .when()
            .get("/marcas")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(6)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/marcas/" + idMarca)
        .then()
            .statusCode(204);
    }

    @Test
    @Order(7)
    public void testFindByIdNotFound() {
        Long idInexistente = 999999L;

        given()
        .when()
            .get("/marcas/" + idInexistente)
        .then()
            .statusCode(404);
    }
}
