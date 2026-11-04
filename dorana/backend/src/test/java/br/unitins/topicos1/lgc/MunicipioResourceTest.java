package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.municipio.dto.MunicipioDTO;
import br.unitins.topicos1.lgc.municipio.dto.MunicipioDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MunicipioResourceTest extends AuthTestBase {

    private static Long idMunicipio;
    private static String tokenAdmin;

    @BeforeAll
    static void setupAuth() {
        tokenAdmin = loginAdmin();
    }

    @Test
    @Order(1)
    public void testFindAll() {
        given()
        .when()
            .get("/municipios")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testCreate() {
        // ID 7 = Tocantins (já populado no import.sql)
        MunicipioDTO dto = new MunicipioDTO("Municipio Teste", 7L);

        MunicipioDTOResponse response = given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/municipios")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/municipios/"))
            .body("id", notNullValue())
            .body("nome", is("Municipio Teste"))
            .extract()
            .as(MunicipioDTOResponse.class);

        idMunicipio = response.id();
    }

    @Test
    @Order(3)
    public void testFindById() {
        given()
        .when()
            .get("/municipios/" + idMunicipio)
        .then()
            .statusCode(200)
            .body("id", is(idMunicipio.intValue()));
    }

    @Test
    @Order(4)
    public void testFindByNomeQueryParam() {
        given()
            .queryParam("nome", "Municipio Teste")
        .when()
            .get("/municipios")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(5)
    public void testUpdate() {
        MunicipioDTO dto = new MunicipioDTO("Municipio Alterado", 7L);

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .put("/municipios/" + idMunicipio)
        .then()
            .statusCode(200)
            .body("id", is(idMunicipio.intValue()))
            .body("nome", is("Municipio Alterado"));
    }

    @Test
    @Order(6)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/municipios/" + idMunicipio)
        .then()
            .statusCode(204);
    }
}
