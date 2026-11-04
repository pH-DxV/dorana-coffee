package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.cafe.dto.CafeDTO;
import br.unitins.topicos1.lgc.cafe.dto.CafeDTOResponse;
import br.unitins.topicos1.lgc.itemPedido.dto.ItemPedidoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EstoqueResourceTest extends AuthTestBase {

    private static String tokenAdmin;
    private static String tokenCliente;
    private static Long idCafe;

    @BeforeAll
    static void setup() {
        tokenAdmin = loginAdmin();
        tokenCliente = loginCliente();
    }

    @Test
    @Order(1)
    public void testIniciaEstoqueNoCadastroDeCafe() {
        CafeDTO dto = new CafeDTO(
            "Café Teste Estoque Integrado",
            "Teste de controle de estoque",
            1L,
            1L,
            1L,
            1L,
            Set.of(),
            85,
            new BigDecimal("40.00"),
            500.0,
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
            .body("estoque", is(100))
            .extract().as(CafeDTOResponse.class);

        idCafe = response.id();
    }

    @Test
    @Order(2)
    public void testBaixaEstoqueAoCriarPedido() {
        // Pedido de 10 unidades do cafe recém-criado (saldo era 100)
        ItemPedidoDTO item = new ItemPedidoDTO(10, idCafe);
        PedidoDTO pedidoDto = new PedidoDTO(1L, List.of(item));

        given()
            .header("Authorization", "Bearer " + tokenCliente)
            .contentType(ContentType.JSON)
            .body(pedidoDto)
        .when()
            .post("/pedidos")
        .then()
            .statusCode(201);

        given()
        .when()
            .get("/cafes/" + idCafe)
        .then()
            .statusCode(200)
            .body("estoque", is(90));
    }

    @Test
    @Order(3)
    public void testErroEstoqueInsuficiente() {
        // Tentativa de compra de 9999 unidades quando o saldo é 90
        ItemPedidoDTO item = new ItemPedidoDTO(9999, idCafe);
        PedidoDTO pedidoDto = new PedidoDTO(1L, List.of(item));

        given()
            .header("Authorization", "Bearer " + tokenCliente)
            .contentType(ContentType.JSON)
            .body(pedidoDto)
        .when()
            .post("/pedidos")
        .then()
            .statusCode(400);
    }
}
