package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.endereco.dto.EnderecoDTO;
import br.unitins.topicos1.lgc.endereco.dto.EnderecoDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnderecoResourceTest extends AuthTestBase {

    private static Long idEndereco;
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
            .get("/enderecos")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testCreate() {
        EnderecoDTO dto = new EnderecoDTO(
            "77000000",
            "Rua das Flores",
            "10",
            "Quadra 10",
            "Centro",
            7L // Palmas/TO
        );

        idEndereco = given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/enderecos")
        .then()
            .statusCode(201)
            .extract()
            .as(EnderecoDTOResponse.class)
            .id();
    }

    @Test
    @Order(3)
    public void testFindById() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/enderecos/" + idEndereco)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(4)
    public void testUpdate() {
        EnderecoDTO dto = new EnderecoDTO(
            "77000123",
            "Rua Nova",
            "20",
            "Qd 2",
            "Bairro Novo",
            7L
        );

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .put("/enderecos/" + idEndereco)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(5)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/enderecos/" + idEndereco)
        .then()
            .statusCode(204);
    }
}
