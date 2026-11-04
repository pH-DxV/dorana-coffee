package br.unitins.topicos1.lgc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.unitins.topicos1.lgc.endereco.dto.EnderecoDTO;
import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTO;
import br.unitins.topicos1.lgc.usuario.dto.AlterarSenhaDTO;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTO;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTOResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioResourceTest extends AuthTestBase {

    private static Long idUsuarioAdmin;
    private static String tokenAdmin;

    @BeforeAll
    static void setup() {
        tokenAdmin = loginAdmin();
    }

    @Test
    @Order(1)
    public void testFindAllComoAdmin() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/usuarios")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    public void testMeEndpoint() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/usuarios/me")
        .then()
            .statusCode(200)
            .body("login", is("admin"));
    }

    @Test
    @Order(3)
    public void testCreateNovoUsuarioAdmin() {
        TelefoneDTO tel = new TelefoneDTO("63", "999998888");
        EnderecoDTO end = new EnderecoDTO("77000000", "Rua Admin", "100", "Sala 1", "Centro", 7L);

        UsuarioDTO dto = new UsuarioDTO(
            "Admin Teste Unitario",
            "admin_teste_unit@email.com",
            "admin_teste_unit",
            "senhaAdmin123",
            "12398745600",
            1, // Perfil ADM
            LocalDate.of(1985, 10, 10),
            List.of(tel),
            List.of(end)
        );

        UsuarioDTOResponse response = given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/usuarios")
        .then()
            .statusCode(201)
            .header("Location", org.hamcrest.Matchers.containsString("/usuarios/"))
            .extract()
            .as(UsuarioDTOResponse.class);

        idUsuarioAdmin = response.id();
    }

    @Test
    @Order(4)
    public void testFindById() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .get("/usuarios/" + idUsuarioAdmin)
        .then()
            .statusCode(200)
            .body("id", is(idUsuarioAdmin.intValue()));
    }

    @Test
    @Order(5)
    public void testAcessoSemTokenRetorna401() {
        given()
        .when()
            .get("/usuarios")
        .then()
            .statusCode(401);
    }

    @Test
    @Order(6)
    public void testAlterarMinhaSenhaIncorretaRetornaErro() {
        AlterarSenhaDTO dto = new AlterarSenhaDTO("senha_errada", "novaSenha123");

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .patch("/usuarios/me/senha")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(7)
    public void testDeleteUsuario() {
        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/usuarios/" + idUsuarioAdmin)
        .then()
            .statusCode(204);
    }
}
