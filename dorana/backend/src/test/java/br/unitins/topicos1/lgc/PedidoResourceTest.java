package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.itemPedido.dto.ItemPedidoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PedidoResourceTest extends AuthTestBase {

    private static String tokenCliente;
    private static String tokenAdmin;
    private static Long idPedidoCriado;

    @BeforeAll
    static void setup() {
        tokenCliente = loginCliente();
        tokenAdmin = loginAdmin();
    }

    @Test
    @Order(1)
    public void testFindAllComoAdmin() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/pedidos")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testFindMeusPedidos() {
        given()
            .header("Authorization", "Bearer " + tokenCliente)
        .when()
            .get("/pedidos/me")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(3)
    public void testCreatePedido() {
        // Cafe 1 e Endereço 1 do Cliente (definidos no import.sql)
        ItemPedidoDTO item = new ItemPedidoDTO(2, 1L);
        PedidoDTO dto = new PedidoDTO(1L, List.of(item));

        PedidoDTOResponse response = given()
            .header("Authorization", "Bearer " + tokenCliente)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/pedidos")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/pedidos/"))
            .body("id", notNullValue())
            .extract()
            .as(PedidoDTOResponse.class);

        idPedidoCriado = response.id();
    }

    @Test
    @Order(4)
    public void testFindById() {
        given()
            .header("Authorization", "Bearer " + tokenCliente)
        .when()
            .get("/pedidos/" + idPedidoCriado)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(5)
    public void testCancelarPedidoComEstorno() {
        given()
            .header("Authorization", "Bearer " + tokenCliente)
            .contentType(ContentType.JSON)
        .when()
            .post("/pedidos/" + idPedidoCriado + "/cancelamento")
        .then()
            .statusCode(200);
    }
}
