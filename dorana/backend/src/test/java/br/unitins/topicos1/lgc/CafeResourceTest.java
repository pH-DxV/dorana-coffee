package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.cafe.dto.CafeDTO;
import br.unitins.topicos1.lgc.cafe.dto.CafeDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CafeResourceTest extends AuthTestBase {

    private static Long idCafe;
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
            .get("/cafes")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testCreate() {
        // Usa Marca 1 e Categoria 1 (populadas no import.sql)
        CafeDTO dto = new CafeDTO(
                "Café Especial Teste",
                "Descrição do café teste.",
                1L,
                1L,
                3L,
                1L,
                Set.of(),
                88,
                new BigDecimal("55.00"),
                250.0,
                100
        );

        CafeDTOResponse response = given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/cafes")
                .then()
                .statusCode(201)
                .header("Location", org.hamcrest.Matchers.containsString("/cafes/"))
                .body("id", notNullValue())
                .body("nome", is("Café Especial Teste"))
                .extract()
                .as(CafeDTOResponse.class);

        idCafe = response.id();
    }

    @Test
    @Order(3)
    public void testFindById() {
        given()
        .when()
            .get("/cafes/" + idCafe)
        .then()
            .statusCode(200)
            .body("id", is(idCafe.intValue()));
    }

    @Test
    @Order(4)
    public void testFindByNomeQueryParam() {
        given()
            .queryParam("nome", "Café Especial Teste")
        .when()
            .get("/cafes")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(5)
    public void testUpdate() {
        CafeDTO dto = new CafeDTO(
                "Café Especial Alterado",
                "Descrição alterada.",
                1L,
                1L,
                3L,
                1L,
                Set.of(),
                90,
                new BigDecimal("60.00"),
                250.0,
                120
        );

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .put("/cafes/" + idCafe)
        .then()
            .statusCode(200)
            .body("nome", is("Café Especial Alterado"));
    }

    @Test
    @Order(6)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/cafes/" + idCafe)
        .then()
            .statusCode(204);
    }
}
