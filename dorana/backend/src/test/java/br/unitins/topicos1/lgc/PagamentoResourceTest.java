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
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoCartaoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagamentoResourceTest extends AuthTestBase {

    private static String tokenCliente;
    private static Long idPedido;

    @BeforeAll
    static void setup() {
        tokenCliente = loginCliente();

        // Cria um pedido para realizar pagamento
        ItemPedidoDTO item = new ItemPedidoDTO(1, 1L);
        PedidoDTO pedidoDto = new PedidoDTO(1L, List.of(item));

        idPedido = given()
            .port(8081)
            .header("Authorization", "Bearer " + tokenCliente)
            .contentType(ContentType.JSON)
            .body(pedidoDto)
        .when()
            .post("/pedidos")
        .then()
            .statusCode(201)
            .extract()
            .as(PedidoDTOResponse.class)
            .id();
    }

    @Test
    @Order(1)
    public void testPagarCartao() {
        PagamentoCartaoDTO dto = new PagamentoCartaoDTO(
            idPedido,
            "Cliente Teste",
            "1234567890123456",
            "VISA",
            "12/28",
            "123"
        );

        given()
            .header("Authorization", "Bearer " + tokenCliente)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/pagamentos/cartao")
        .then()
            .statusCode(201)
            .body("id", notNullValue());
    }
}
