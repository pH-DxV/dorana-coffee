package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.cliente.dto.ClienteDTO;
import br.unitins.topicos1.lgc.cliente.dto.ClienteDTOResponse;
import br.unitins.topicos1.lgc.endereco.dto.EnderecoDTO;
import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClienteResourceTest extends AuthTestBase {

    private static Long idCliente;
    private static String tokenAdmin;

    @BeforeAll
    static void setup() {
        tokenAdmin = loginAdmin();
    }

    @Test
    @Order(1)
    public void testCreateClienteAutocadastro() {
        TelefoneDTO tel = new TelefoneDTO("63", "988776655");
        EnderecoDTO end = new EnderecoDTO("77000000", "Rua Cliente", "10", "Apto 1", "Centro", 7L);

        ClienteDTO dto = new ClienteDTO(
            "Cliente Teste Unitario",
            "cliente_novo_teste@email.com",
            "cliente_novo_teste",
            "123456",
            "98765432100",
            LocalDate.of(1990, 1, 1),
            List.of(tel),
            List.of(end)
        );

        ClienteDTOResponse response = given()
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/clientes")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/clientes/"))
            .body("id", notNullValue())
            .extract()
            .as(ClienteDTOResponse.class);

        idCliente = response.id();
    }

    @Test
    @Order(2)
    public void testFindById() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/clientes/" + idCliente)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(3)
    public void testFindAll() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/clientes")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(4)
    public void testDelete() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/clientes/" + idCliente)
        .then()
            .statusCode(204);
    }
}
