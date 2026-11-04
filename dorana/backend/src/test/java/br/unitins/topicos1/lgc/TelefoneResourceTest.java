package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTO;
import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TelefoneResourceTest extends AuthTestBase {

    private static Long idTelefone;
    private static String tokenAdmin;

    @BeforeAll
    static void setup() {
        tokenAdmin = loginAdmin();
    }

    @Test
    @Order(1)
    public void testFindAll() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/telefones")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testCreate() {
        TelefoneDTO dto = new TelefoneDTO("63", "988776655");

        TelefoneDTOResponse response = given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/telefones")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/telefones/"))
            .body("id", notNullValue())
            .body("ddd", is("63"))
            .extract()
            .as(TelefoneDTOResponse.class);

        idTelefone = response.id();
    }

    @Test
    @Order(3)
    public void testFindById() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/telefones/" + idTelefone)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(4)
    public void testUpdate() {
        TelefoneDTO dto = new TelefoneDTO("62", "911223344");

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .put("/telefones/" + idTelefone)
        .then()
            .statusCode(200)
            .body("id", is(idTelefone.intValue()))
            .body("ddd", is("62"));
    }

    @Test
    @Order(5)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/telefones/" + idTelefone)
        .then()
            .statusCode(204);
    }
}
